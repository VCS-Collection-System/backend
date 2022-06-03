package com.redhat.service;

import static com.redhat.service.Constants.ATTACHMENT_FIELD_NAME;
import static com.redhat.service.Constants.COVID_TEST_RESULT_SUBMISSION_WORKFLOW;
import static com.redhat.service.Constants.DOCUMENT_FIELD_NAME;
import static com.redhat.service.Constants.EMPLOYEE_FIELD_NAME;
import static com.redhat.service.Constants.KJAR_DEPLOYMENT_ID;
import static com.redhat.service.Constants.VACCINE_CARD_REVIEW_WORKFLOW;
import static com.redhat.service.Constants.CONFIG_FIELD_NAME;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.redhat.service.validation.AttestationValidator;
import com.redhat.service.validation.ImageValidator;
import com.redhat.vcs.model.Attachment;
import com.redhat.vcs.model.CountryConfigurations;
import com.redhat.vcs.model.CovidTestResultDocument;
import com.redhat.vcs.model.Document;
import com.redhat.vcs.model.Employee;
import com.redhat.vcs.model.Proof;
import com.redhat.vcs.model.Vaccination;
import com.redhat.vcs.model.VaccineCardDocument;
import com.redhat.vcs.model.VaccineDocumentList;
import com.redhat.vcs.model.VaxProofAutomaticApprovalRequest;

import org.jbpm.services.api.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController
@RequestMapping("/attestation")
public class AttestationController {

    private static final Logger LOG = LoggerFactory.getLogger(AttestationController.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private AttestationValidator attestationValidator;

    @Autowired
    private CustomQueryService queryService;

    @Autowired
    private ImageValidator imageValidator;

    @Autowired
    KeycloakAuthUtil authUtil;

    @PersistenceContext
    private EntityManager em;

    @Value("${s3.bucket.name}")
    private String bucketName;

    @Value("${enable.s3.persistence:false}")
    private boolean s3PersistenceEnabled;

    @Value("${aas.enabled:false}")
    private boolean aasEnabled;

    @Value("${aas.proofs.endpoint}")
    private String aasProofsEndpoint;

    @Value("${enable.s3.testimage:false}")
    private boolean testImageEnabled;

    @Value("${s3.testimage.filename:TestImage.png}")
    private String testImageName;

    @PostMapping("covid-test-result")
    public ResponseEntity<String> covidTestResultUpload
        (
            Authentication authentication,
            @RequestPart(name = EMPLOYEE_FIELD_NAME) Employee employeeIn,
            @RequestPart(name = DOCUMENT_FIELD_NAME) CovidTestResultDocument document,
            @RequestPart(name = ATTACHMENT_FIELD_NAME) MultipartFile file
        ) {

        try {
            validateToken(authentication, employeeIn, document);
            if(employeeIn.getAgencyName() != null && (employeeIn.getAgencyName().equalsIgnoreCase("AUT") || employeeIn.getAgencyName().equalsIgnoreCase("DEU"))){
                employeeIn.setVaxConsentDate(document.getSubmissionDate());
            }
            prepareDocumentAndPersistToS3(Arrays.asList(document), employeeIn, file);
            // Start Process
            Map<String, Object> params = Collections.singletonMap("document", document);
            long pid = processService.startProcess(KJAR_DEPLOYMENT_ID, COVID_TEST_RESULT_SUBMISSION_WORKFLOW, params);
            LOG.info("COVID test result submission workflow started. PID:\n\t{}", pid);
            LOG.debug("Document submitted:\n\t{}", document);

            return ResponseEntity.accepted().build();
        } catch (AuthorizationException ae) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ae.getMessage());
        } catch (Exception e) {
            LOG.error("Error while processing COVID test result document", e);
            LOG.debug("Employee info and document submitted:\n\t{}\n\t{}", employeeIn, document);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("vax")
    public ResponseEntity<String> vaccineDocumentUpload
        (
            Authentication authentication,
            @RequestPart(name = EMPLOYEE_FIELD_NAME) Employee employeeIn,
            @RequestPart(name = DOCUMENT_FIELD_NAME) List<VaccineCardDocument> documents,
            @RequestPart(name = ATTACHMENT_FIELD_NAME) MultipartFile file
        ) {

        try {
            validateToken(authentication, employeeIn, documents.get(0));
            populateEmployeeDetails(documents.get(0),employeeIn);
            prepareDocumentAndPersistToS3(new ArrayList<>(documents), employeeIn, file);
            VaxProofAutomaticApprovalRequest vaxProofAutomaticApprovalRequest
                    = createVaxProofAutomaticApprovalRequest(employeeIn, documents, file);

            VaccineDocumentList documentList = new VaccineDocumentList();
            documentList.setDocuments(documents);
            String employeeAgencyName = documents.get(0).getEmployee().getAgencyName().toLowerCase();
            String employeeId = documents.get(0).getEmployee().getEmployeeId();
            String firstName = documents.get(0).getEmployee().getFirstName();
            String lastName = documents.get(0).getEmployee().getLastName();

            // Start Process
            Map<String, Object> params = new HashMap<>();
            params.put("documentList", documentList);
            params.put("vaxProofAutomaticApprovalRequest", vaxProofAutomaticApprovalRequest);
            params.put("aasEnabled", aasEnabled);
            params.put("aasProofsEndpoint", aasProofsEndpoint);
            params.put("employeeAgencyName",employeeAgencyName);
            params.put("employeeId",employeeId);
            params.put("firstName",firstName);
            params.put("lastName",lastName);
            long pid = processService.startProcess(KJAR_DEPLOYMENT_ID, VACCINE_CARD_REVIEW_WORKFLOW, params);
            LOG.info("Vaccine document submission workflow started. PID:\n\t{}", pid);
            LOG.debug("Document submitted:\n\t{}", documents);

            return ResponseEntity.accepted().build();
        } catch (AuthorizationException ae) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ae.getMessage());
        } catch (Exception e) {
            LOG.error("Error while processing vaccine document", e);
            LOG.debug("Employee info and document submitted:\n\t{}\n\t{}", employeeIn, documents);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("attachment")
    public ResponseEntity<Resource> vaccineDocumentDownload(@RequestBody Attachment attachment) {
        try {
            return retrieveS3Document(attachment);
        } catch (Exception e) {
            LOG.error("Error while retrieving s3 data", e);
            LOG.debug("Attachment:\n\t{}", attachment);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("country-configs")
    public void countryConfigUpload
        ( 
             Authentication authentication,
             @RequestPart(name = CONFIG_FIELD_NAME) CountryConfigurations countryConfig
        ) {
        try {
            if(authUtil.isSuperUser(authentication) ){

                queryService.persistCountryConfigurations(countryConfig);
            }
                
            } catch (Exception e) {
                LOG.error("Error while uploading configurations", e);
                LOG.debug("agency name:\n\t{}", countryConfig);
            }
        }


    @DeleteMapping("delete-vax-record/{id}")
    public void DeleteVaccineRecord( @PathVariable Long id ) 
    {
        VaccineCardDocument document= em.find(VaccineCardDocument.class, id);
        if(document!=null)
        {
            queryService.deleteVaccineRecordById(id);
            deleteVaccineDocumentFromS3(document); 
        }
           
    }

    private void deleteVaccineDocumentFromS3(VaccineCardDocument document){
        if(document.getAttachment() != null && org.apache.commons.lang3.StringUtils.isNotBlank(document.getAttachment().getS3UUID())){
            s3Service.delete(document.getAttachment().getS3UUID());
        }
    }

    private ResponseEntity<Resource> retrieveS3Document(Attachment attachment){
        if (attachment == null) {
            LOG.error("Error null attachment");
            throw new RuntimeException("Attachment is missing");
        }

        if (attachment.getS3UUID() == null) {
            LOG.error("Error attachment.getS3UUID() = null");
            throw new RuntimeException("S3 uuid is missing");
        }

        String s3DocumentId = attachment.getS3UUID();
        byte[] bytes = null;

        try {
            bytes = s3Service.get(s3DocumentId);
        } catch (S3Exception s3e) {
            LOG.warn("Failed to retrieve s3 data", s3e);
        }

        if (testImageEnabled && (bytes == null || bytes.length == 0) ){
            LOG.info("Failed to find image in s3. Loading test image.");
            bytes = loadTestImage();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getOriginalFileName() + "\"")
                .body(new ByteArrayResource(bytes));
    }

    private byte[] loadTestImage() {
        byte[] bytes = null;
        InputStream ioStream = this.getClass().getClassLoader().getResourceAsStream(testImageName);

        if (ioStream == null) {
            LOG.warn("Unable to load test image");
        } else {
            try {
                bytes = ioStream.readAllBytes();
            } catch (IOException ioe) {
                LOG.warn("Unable to load test image", ioe);
            }
        }

        return bytes;
    }

    private void prepareDocumentAndPersistToS3(
            List<Document> documents,
            Employee employeeIn,
            MultipartFile file) throws S3Exception, IOException {

        validateEmployee(employeeIn);
        validateDocument(documents.get(0));
        validateImage(file); 
        Employee employee = employeeService.update(employeeIn);
        Attachment attachment = createAttachment(file);
        documents.forEach(document -> {

            document.setEmployee(employee);
            document.setSubmissionDate(LocalDateTime.now());
            document.setAttachment(attachment);

    });

        // persist document in AWS S3
        if (s3PersistenceEnabled) {
            LOG.debug(">>> S3 bucket: {}", bucketName);
            String s3uuid = s3Service.put(file.getBytes());
            attachment.setS3UUID(s3uuid);
        } else {
            LOG.warn("AWS S3 is currently disabled. Set enable.s3.persistence=true to enable it.");
        }
    }

    private Attachment createAttachment(MultipartFile file) {
        Attachment attachment = new Attachment();

        attachment.setOriginalFileName(file.getOriginalFilename());
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setS3BucketName(bucketName);

        return attachment;
    }

    /**
     * Validate the document; on failure, throw an exception
     *
     * @param document the document to validate
     * @throws IllegalArgumentException on validation failure
     */
    private void validateDocument(Document document) {
        String error = attestationValidator.validateDocument(document);

        if (error != null) {
            throw new IllegalArgumentException(error);
        }
    }

    /**
     * Validate the employee; on failure, throw an exception
     *
     * @param employee the employee to validate
     * @throws IllegalArgumentException on validation failure
     */
    private void validateEmployee(Employee employee) {
        String error = attestationValidator.validateEmployee(employee);

        if (error != null) {
            throw new IllegalArgumentException(error);
        }
    }

    /**
     * Check if the employee first and last name match the one in sso token; throw an exception on failure
     *
     * @param authentication Authentication piece storing access token
     * @param employee Employee record
     * @param document Document record
     * @throws AuthorizationException on validation failure
     */
    private void validateToken(Authentication authentication, Employee employee, Document document) {
        String error = attestationValidator.validateToken(authentication, employee, document);

        if (error != null) {
            throw new AuthorizationException(error);
        }
    }

    private void addVaccineCardDocumentToList( List<VaccineCardDocument> documents, List<Vaccination> vaccinations ) {
        
        documents.forEach(document -> {
            Vaccination vaccination = new Vaccination();
            vaccination.setVaccineType(document.getVaccineBrand().name());

            if (document.getVaccineAdministrationDate() != null){
                vaccination.setInoculationDate(document.getVaccineAdministrationDate().toString());
            } else {
                LOG.error("Expected a valid inoculation date but got null.");
            }

            vaccination.setLotNumber(document.getLotNumber());
            vaccinations.add(vaccination);
        });
    }

    private VaxProofAutomaticApprovalRequest createVaxProofAutomaticApprovalRequest(Employee employee,
            List<VaccineCardDocument> documents, MultipartFile file){
        VaxProofAutomaticApprovalRequest automaticApprovalRequest = new VaxProofAutomaticApprovalRequest();

        List<Vaccination> vaccinations = new ArrayList<>();

        addVaccineCardDocumentToList(documents, vaccinations);

        List<Proof> proofs = new ArrayList<>();

        Proof proof = new Proof();
        proof.setType(documents.get(0).getProofType().toString().toLowerCase());
    
        proof.setVaccinations(vaccinations);

        try {
            if (file.getBytes() != null) {
                LOG.debug("<<<< IMAGE FILE NAME= " + documents.get(0).getAttachment().getOriginalFileName());
                LOG.debug("<<<< IMAGE FILE CONTENT TYPE= " + documents.get(0).getAttachment().getContentType());
                String imageExt = documents.get(0).getAttachment().getContentType();

                LOG.debug("<<< IMAGE EXT: " + imageExt.toLowerCase());
                proof.setImage("data:" + imageExt.toLowerCase() + ";base64," + Base64.getEncoder().encodeToString(file.getBytes()));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        proofs.add(proof);

        automaticApprovalRequest.setFirstName(employee.getFirstName());
        automaticApprovalRequest.setLastName(employee.getLastName());

        if (employee.getDateOfBirth() != null){
            automaticApprovalRequest.setDob(employee.getDateOfBirth().toString());
        } else {
            automaticApprovalRequest.setDob("1970-01-01");

        }

        automaticApprovalRequest.setProofs(proofs);

        return automaticApprovalRequest;
    }

    /**
     * Check if the image is valid
     *
     * @param file the image file to validate
     * @throws IllegalArgumentException on validation failure
     */
    private void validateImage(MultipartFile file) {
        String error = imageValidator.validateUploadedImage(file);

        if (error != null) {
            throw new IllegalArgumentException(error);
        }
    }

    /**
     *
     * @param document the vax card document submitted
     * @param employeeIn
     * Populate Employee details
     * Sets the consent date if the country requires capturing the consent date.
     */
    public void populateEmployeeDetails(VaccineCardDocument document,Employee employeeIn){
        // we are currently setting consent date as document submission date for all regions

             employeeIn.setVaxConsentDate(document.getSubmissionDate());
        
    }
}
