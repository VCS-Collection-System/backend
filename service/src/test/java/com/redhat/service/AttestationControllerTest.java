package com.redhat.service;

import static com.redhat.service.Constants.COVID_TEST_RESULT_SUBMISSION_WORKFLOW;
import static com.redhat.service.Constants.KJAR_DEPLOYMENT_ID;
import static com.redhat.service.Constants.VACCINE_CARD_REVIEW_WORKFLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.redhat.service.validation.AttestationValidator;
import com.redhat.service.validation.ImageValidator;
import com.redhat.vcs.model.Attachment;
import com.redhat.vcs.model.CovidTestResultDocument;
import com.redhat.vcs.model.Employee;
import com.redhat.vcs.model.Proof;
import com.redhat.vcs.model.ProofType;
import com.redhat.vcs.model.Vaccination;
import com.redhat.vcs.model.VaccineBrand;
import com.redhat.vcs.model.VaccineCardDocument;
import com.redhat.vcs.model.VaccineDocumentList;
import com.redhat.vcs.model.VaxProofAutomaticApprovalRequest;

import org.jbpm.services.api.ProcessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

/**
 * This class contains the unit (logic / flow) tests for AttestationController.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 *
 * TODO: Now that validation is in its own class, many of these tests set up far too much data; need major test refactor
 */
@ExtendWith(MockitoExtension.class)
public class AttestationControllerTest{
    @Mock
    private EntityManager em;

    @Mock
    private EmployeeService employeeServiceMock;

    @Mock
    private ProcessService processServiceMock;

    @Mock
    private S3Service s3ServiceMock;

    @Mock
    private AttestationValidator attestationValidatorMock;

    @Mock
    private ImageValidator imageValidatorMock;

    @InjectMocks
    private AttestationController objectUnderTest_attestationController;

    @Captor
    private ArgumentCaptor<Map<String, Object>> paramsCaptor;

    /**
     * This "vaccineDocumentDownload(Attachment)" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - s3ServiceMock.get will return a byte array from a dummy test String
     *
     * Calling the method under test should return a ResponseEntity<Resource> which:
     *     - Contains a 200 response code
     *     - Contains a "Content-Disposition" header which includes the filename
     *     - Contains a "Content-Type" header of "application/xml" (per passed-in attachment)"
     *     - Contains the byte array from the s3ServiceMock.get call, as a ByteArrayResource
     */
    @Test
    public void test_vaccineDocumentDownload_success() {
        // Set up data
        final String UUID = "abcdefg";
        final String MISC_DATA = "What does the fox say?";
        final String FILENAME = "autoexec.bat";

        Attachment attachment = new Attachment();
        attachment.setS3UUID(UUID);
        attachment.setContentType(MediaType.APPLICATION_XML.toString());
        attachment.setOriginalFileName(FILENAME);

        // Set up mocks
        when(s3ServiceMock.get(UUID))
                .thenReturn(MISC_DATA.getBytes());

        // Run test
        ResponseEntity<Resource> response = objectUnderTest_attestationController.vaccineDocumentDownload(attachment);

        // Validate
        assertEquals(
                HttpStatus.OK,
                response.getStatusCode(),
                "Should return HTTP 200 on vaccineDocumentDownload success");

        assertEquals(
                MediaType.APPLICATION_XML,
                response.getHeaders().getContentType(),
                "Response content type should be application/xml, per passed-in attachment");
        assertEquals(
                Collections.singletonList("attachment; filename=\"" + FILENAME + "\""),
                response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION),
                "Content-Disposition should list the original filename");
        assertEquals(
                new ByteArrayResource(MISC_DATA.getBytes()),
                response.getBody(),
                "Response body should match the return value from s3Service.get(String)");
    }

    /**
     * This "vaccineDocumentDownload(Attachment)" test is a negative path validation, for the following condition(s):
     *     - Provided Attachment is null
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a ResponseEntity<Resource> which:
     *     - Contains a 400 response code
     *     - Has a null response body
     */
    @Test
    public void test_vaccineDocumentDownload_fail_nullAttachment() {
        // Set up data

        // Set up mocks

        // Run test
        ResponseEntity<Resource> response = objectUnderTest_attestationController.vaccineDocumentDownload(null);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when attachment is null");

        assertNull(
                response.getBody(),
                "Response body should be null on failure");
    }

    /**
     * This "vaccineDocumentDownload(Attachment)" test is a negative path validation, for the following condition(s):
     *     - Provided Attachment has no UUID
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a ResponseEntity<Resource> which:
     *     - Contains a 400 response code
     *     - Has a null response body
     */
    @Test
    public void test_vaccineDocumentDownload_fail_nullUUID() {
        // Set up data
        Attachment attachment = new Attachment();
        attachment.setS3UUID(null);

        // Set up mocks

        // Run test
        ResponseEntity<Resource> response = objectUnderTest_attestationController.vaccineDocumentDownload(null);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when UUID is null");

        assertNull(
                response.getBody(),
                "Response body should be null on failure");
    }

   /**
     * This "vaccineDocumentDownload(Attachment)" test is a happyish path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - s3ServiceMock.get will return a null byte array
     *
     * Calling the method under test should return a ResponseEntity<Resource> which:
     *     - Contains a 200 response code
     *     - Contains a "Content-Disposition" header which includes the filename
     *     - Contains a "Content-Type" header of "application/xml" (per passed-in attachment)"
     *     - Contains the byte array containing our test image
     */
    @Test
    public void test_vaccineDocumentDownload_noimage_success() throws IOException{
        // Set up data
        final String UUID = "abcdefg";
        final String FILENAME = "autoexec.bat";

        Attachment attachment = new Attachment();
        attachment.setS3UUID(UUID);
        attachment.setContentType(MediaType.APPLICATION_XML.toString());
        attachment.setOriginalFileName(FILENAME);

        // Set up mocks
        when(s3ServiceMock.get(UUID))
                .thenReturn(null);

        ReflectionTestUtils.setField(objectUnderTest_attestationController,"testImageEnabled" , true);
        ReflectionTestUtils.setField(objectUnderTest_attestationController,"testImageName" , "TestImage.png");

        // Run test
        ResponseEntity<Resource> response = objectUnderTest_attestationController.vaccineDocumentDownload(attachment);

        // Validate
        assertEquals(
                HttpStatus.OK,
                response.getStatusCode(),
                "Should return HTTP 200 on vaccineDocumentDownload success");

        assertEquals(
                MediaType.APPLICATION_XML,
                response.getHeaders().getContentType(),
                "Response content type should be application/xml, per passed-in attachment");
        assertEquals(
                Collections.singletonList("attachment; filename=\"" + FILENAME + "\""),
                response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION),
                "Content-Disposition should list the original filename");

        InputStream ioStream = this.getClass().getClassLoader().getResourceAsStream("TestImage.png");
        byte[] imageBytes = null;
        imageBytes = ioStream.readAllBytes();

        assertEquals(
                new ByteArrayResource(imageBytes),
                response.getBody(),
                "Response body should be non null, containing test image data");
    }

   /**
     * This "vaccineDocumentDownload(Attachment)" test is a happyish path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - s3ServiceMock.get will thow an S3Exception
     *
     * Calling the method under test should return a ResponseEntity<Resource> which:
     *     - Contains a 200 response code
     *     - Contains a "Content-Disposition" header which includes the filename
     *     - Contains a "Content-Type" header of "application/xml" (per passed-in attachment)"
     *     - Contains the byte array containing our test image
     */
    @Test
    public void test_vaccineDocumentDownload_s3exception_success() throws IOException{
        // Set up data
        final String UUID = "abcdefg";
        final String FILENAME = "autoexec.bat";

        Attachment attachment = new Attachment();
        attachment.setS3UUID(UUID);
        attachment.setContentType(MediaType.APPLICATION_XML.toString());
        attachment.setOriginalFileName(FILENAME);

        // Set up mocks
        when(s3ServiceMock.get(UUID))
                .thenThrow(NoSuchKeyException.builder().message("Mock S3 Exception").build());

        ReflectionTestUtils.setField(objectUnderTest_attestationController,"testImageEnabled" , true);
        ReflectionTestUtils.setField(objectUnderTest_attestationController,"testImageName" , "TestImage.png");

        // Run test
        ResponseEntity<Resource> response = objectUnderTest_attestationController.vaccineDocumentDownload(attachment);

        // Validate
        assertEquals(
                HttpStatus.OK,
                response.getStatusCode(),
                "Should return HTTP 200 on vaccineDocumentDownload success");

        assertEquals(
                MediaType.APPLICATION_XML,
                response.getHeaders().getContentType(),
                "Response content type should be application/xml, per passed-in attachment");
        assertEquals(
                Collections.singletonList("attachment; filename=\"" + FILENAME + "\""),
                response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION),
                "Content-Disposition should list the original filename");

        InputStream ioStream = this.getClass().getClassLoader().getResourceAsStream("TestImage.png");
        byte[] imageBytes = null;
        imageBytes = ioStream.readAllBytes();

        assertEquals(
                new ByteArrayResource(imageBytes),
                response.getBody(),
                "Response body should be non null, containing test image data");
    }

   /**
     * This "vaccineDocumentDownload(Attachment)" test is a failure path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - s3ServiceMock.get will thow an S3Exception
     *
     * Calling the method under test should return a ResponseEntity<Resource> which:
     *     - Contains a 400 response code
     */
    @Test
    public void test_vaccineDocumentDownload_unknown_testimage_failure() throws IOException{
        // Set up data
        final String UUID = "abcdefg";
        final String FILENAME = "autoexec.bat";

        Attachment attachment = new Attachment();
        attachment.setS3UUID(UUID);
        attachment.setContentType(MediaType.APPLICATION_XML.toString());
        attachment.setOriginalFileName(FILENAME);

        // Set up mocks

        when(s3ServiceMock.get(UUID))
                .thenThrow(NoSuchKeyException.builder().message("Mock S3 Exception").build());

        ReflectionTestUtils.setField(objectUnderTest_attestationController,"testImageEnabled" , true);
        ReflectionTestUtils.setField(objectUnderTest_attestationController,"testImageName" , "bogus.png");

        // Run test
        ResponseEntity<Resource> response = objectUnderTest_attestationController.vaccineDocumentDownload(attachment);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 on vaccineDocumentDownload failure");

    }

    /**
     * This "vaccineDocumentUpload(Authentication, Employee, VaccineCardDocument, MultipartFile)" test is a happy path
     * validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - A mock multipart file will return various properties, as required by the workflow
     *     - The EntityManager will return a VaccineCardDocument on the first getSingleResult, null on subsequent calls
     *     - EmployeeService.update will return the Employee, unchanged
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 202 response code
     *     - Has a null response body
     *
     * The following interactions are verified, in addition to the return value:
     *     - employeeServiceMock.update should be called once
     *     - processServiceMock.startProcess should be called once
     *         - The Map passed into this object will be captured, in order to verify values from it
     *     - These following MultipartFile fields should be accessed once each: size, content type, original filename
     */
    @Test
    public void test_vaccineDocumentUpload_success() {
        // Set up data
        final ProofType PROOF_TYPE = ProofType.CDC;
        final VaccineBrand VACCINE_BRAND = VaccineBrand.JANSSEN;

        Employee employee = new Employee();
        employee.setId(42L);
        employee.setAgencyCode("Delivery");
        employee.setAgencyName("USA");
        employee.setEmail("fry@planetexpress.ny");

        VaccineCardDocument vaccineCardDocument = new VaccineCardDocument();
        
        vaccineCardDocument.setSubmittedBy("Philip J. Fry");
        vaccineCardDocument.setVaccineShotNumber(2);
        vaccineCardDocument.setVaccineAdministrationDate(LocalDate.now());
        vaccineCardDocument.setVaccineBrand(VACCINE_BRAND);
        vaccineCardDocument.setProofType(PROOF_TYPE);
        vaccineCardDocument.setEmployee(employee);
        List<VaccineCardDocument> vaccineCardDocuments = new ArrayList<VaccineCardDocument>(){{
                add(vaccineCardDocument); 
         }};
        VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
        vaccineDocumentList.setDocuments(vaccineCardDocuments);
        VaccineCardDocument dbDocument = new VaccineCardDocument();
        dbDocument.setVaccineBrand(VACCINE_BRAND);
        dbDocument.setVaccineAdministrationDate(LocalDate.now());
        dbDocument.setLotNumber("X");

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);
        MultipartFile multipartFileMock = mock(MultipartFile.class);

        when(multipartFileMock.getOriginalFilename())
                .thenReturn("arewethebaddies.gif");
        when(multipartFileMock.getContentType())
                .thenReturn("image/gif");
        when(multipartFileMock.getSize())
                .thenReturn(1000000L);

        TypedQuery<VaccineCardDocument> query = mock(TypedQuery.class);

        when(employeeServiceMock.update(employee))
                .thenReturn(employee);

        // Run test
        ResponseEntity<String> response = objectUnderTest_attestationController
                .vaccineDocumentUpload(authenticationMock, employee, vaccineCardDocuments, multipartFileMock);

        // Capture the Map<String, Object> passed into processServiceMock.startProcess:
        verify(processServiceMock, times(1))
                .startProcess(eq(KJAR_DEPLOYMENT_ID), eq(VACCINE_CARD_REVIEW_WORKFLOW), paramsCaptor.capture());

        // Validate
        assertEquals(
                HttpStatus.ACCEPTED,
                response.getStatusCode(),
                "Should return HTTP 202 when upload is successful");

        assertNull(
                response.getBody(),
                "The response body should be null on successful upload");

        Map<String, Object> params = paramsCaptor.getValue();

        assertNotNull(
                params,
                "Parameter map should not be null");

        assertFalse(
                params.isEmpty(),
                "Parameter map should not be empty");

        VaxProofAutomaticApprovalRequest approvalRequest
                = (VaxProofAutomaticApprovalRequest) params.get("vaxProofAutomaticApprovalRequest");

        assertNotNull(
                approvalRequest,
                "Key=vaxProofAutomaticApprovalRequest should not be empty");

        assertNotNull(
                approvalRequest.getProofs(),
                "List of proof should not be null");

        assertFalse(
                approvalRequest.getProofs().isEmpty(),
                "List of proof should not be empty");

        Proof proof = approvalRequest.getProofs().stream().findFirst().get();

        assertEquals(
                "cdc",
                proof.getType(),
                "Proof type should have been hard-coded to 'cdc'");

        assertNotNull(
                proof.getVaccinations(),
                "List of vaccinations should not be null");

        assertFalse(
                proof.getVaccinations().isEmpty(),
                "List of vaccinations should not be empty");

        Vaccination vaccination = proof.getVaccinations().stream().findFirst().get();

        assertEquals(
                VACCINE_BRAND.name(),
                vaccination.getVaccineType(),
                "Vaccine type should match the name of the brand passed in via the document");

        verify(employeeServiceMock, times(1)).update(employee);
        verify(multipartFileMock, times(1)).getOriginalFilename();
        verify(multipartFileMock, times(1)).getContentType();
        verify(multipartFileMock, times(1)).getSize();

        // TODO: How to override @Value s3PersistenceEnabled, so we can verify the s3Service is invoked?
        //verify(s3ServiceMock, times(1)).put(any());
    }

    /**
     * This "vaccineDocumentUpload(Authentication, Employee, VaccineCardDocument, MultipartFile)" test is a negative
     * path validation, for the following condition(s):
     *     - Token validation fails
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - attestationValidatorMock.validateToken returns a non-null (i.e., error) String
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Has an error message indicating the failure case as the response body
     */
    @Test
    public void test_vaccineDocumentUpload_fail_invalidToken() {
        // Set up data
        final String TOKEN_ERROR_MESSAGE = "Invalid token!";

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        VaccineCardDocument vaccineCardDocument = mock(VaccineCardDocument.class);
        List<VaccineCardDocument> vaccineCardDocuments = new ArrayList<VaccineCardDocument>(){{
                add(vaccineCardDocument); 
         }};
        Employee employee = mock(Employee.class);

        when(attestationValidatorMock.validateToken(authenticationMock, employee, vaccineCardDocument))
                .thenReturn(TOKEN_ERROR_MESSAGE);

        // Run test
        ResponseEntity<String> response = objectUnderTest_attestationController
                .vaccineDocumentUpload(authenticationMock, employee, vaccineCardDocuments, multipartFileMock);

        // Validate
        assertEquals(
                HttpStatus.FORBIDDEN,
                response.getStatusCode(),
                "Should return HTTP 400 when last name is invalid");
        assertEquals(
                TOKEN_ERROR_MESSAGE,
                response.getBody(),
                "Response body should indicate that an error occurred");
    }

    /**
     * This "vaccineDocumentUpload(Authentication, Employee, VaccineCardDocument, MultipartFile)" test is a negative
     * path validation, for the following condition(s):
     *     - Employee validation fails
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - attestationValidatorMock.validateEmployee returns a non-null (i.e., error) String
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Has an error message indicating the failure case as the response body
     */
    @Test
    public void test_vaccineDocumentUpload_fail_invalidEmployee() {
        // Set up data
        final String EMPLOYEE_ERROR_MESSAGE = "Invalid employee!";

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        Employee employee = new Employee();
        employee.setAgencyName("USA");
        VaccineCardDocument vaccineCardDocument = new VaccineCardDocument();
        List<VaccineCardDocument> vaccineCardDocuments = new ArrayList<VaccineCardDocument>(){{
                add(vaccineCardDocument); 
         }};

        when(attestationValidatorMock.validateEmployee(employee))
                .thenReturn(EMPLOYEE_ERROR_MESSAGE);

        // Run test
        ResponseEntity<String> response = objectUnderTest_attestationController
                .vaccineDocumentUpload(authenticationMock, employee, vaccineCardDocuments, multipartFileMock);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when employee id and/or agency are invalid");
        assertEquals(
                EMPLOYEE_ERROR_MESSAGE,
                response.getBody(),
                "Response body should include both validation errors (invalid employee id, invalid agency)");
    }

    /**
     * This "vaccineDocumentUpload(Authentication, Employee, VaccineCardDocument, MultipartFile)" test is a negative
     * path validation, for the following condition(s):
     *     - Document validation fails
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - attestationValidatorMock.validateDocument returns a non-null (i.e., error) String
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Has an error message indicating the failure case as the response body
     */
    @Test
    public void test_vaccineDocumentUpload_fail_invalidDocument() {
        // Set up data
        final String DOCUMENT_ERROR_MESSAGE = "Invalid document!";

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        Employee employee = new Employee();
        employee.setAgencyName("USA");
        VaccineCardDocument vaccineCardDocument = new VaccineCardDocument();
        List<VaccineCardDocument> vaccineCardDocuments = new ArrayList<VaccineCardDocument>(){{
                add(vaccineCardDocument); 
         }};

        when(attestationValidatorMock.validateDocument(vaccineCardDocument))
                .thenReturn(DOCUMENT_ERROR_MESSAGE);

        // Run test
        ResponseEntity<String> response = objectUnderTest_attestationController
                .vaccineDocumentUpload(authenticationMock, employee, vaccineCardDocuments, multipartFileMock);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when too few shots have been listed");
        assertEquals(
                DOCUMENT_ERROR_MESSAGE,
                response.getBody(),
                "Response body should indicate that an error occurred validating the document");
    }


    /**
     * This "vaccineDocumentUpload(Authentication, Employee, VaccineCardDocument, MultipartFile)" test is a negative
     * path validation, for the following condition(s):
     *     - Image validation fails
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - imageValidatorMock.validateUploadedImage returns a non-null (i.e., error) String
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Has an error message indicating the failure case as the response body
     */
    @Test
    public void test_vaccineDocumentUpload_fail_imageValidationFailure() {
        // Set up data
        final String IMAGE_ERROR_MESSAGE = "Invalid image!";

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        Employee employee = new Employee();
        employee.setAgencyName("USA");
        VaccineCardDocument vaccineCardDocument = new VaccineCardDocument();
        List<VaccineCardDocument> vaccineCardDocuments = new ArrayList<VaccineCardDocument>(){{
                add(vaccineCardDocument); 
         }};
        when(imageValidatorMock.validateUploadedImage(multipartFileMock))
                .thenReturn(IMAGE_ERROR_MESSAGE);

        // Run test
        ResponseEntity<String> response = objectUnderTest_attestationController
                .vaccineDocumentUpload(authenticationMock, employee, vaccineCardDocuments, multipartFileMock);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when test result is invalid");
        assertEquals(
                IMAGE_ERROR_MESSAGE,
                response.getBody(),
                "Response body should indicate that document validation failed");
    }

    /**
     * This "vaccineDocumentUpload(Authentication, Employee, VaccineCardDocument, MultipartFile)" test is a negative
     * path validation, for the following condition(s):
     *     - Failed to parse proof image
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - file.getBytes will throw an IOException
     *     - The EntityManager will return a VaccineCardDocument on the first getSingleResult, null on subsequent calls
     *     - EmployeeService.update will return the Employee, unchanged
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Has an error message indicating both failure cases (invalid number of shots) as the response body
     *
     * @throws IOException This is a required throws statement, due to setting up a mock with a checked Exception
     */
    @Test
    public void test_vaccineDocumentUpload_fail_badImage() throws IOException {
        // Set up data
        final String ERROR = "Man, this ain't an image, this is a CELL PHONE!!";

        final ProofType PROOF_TYPE = ProofType.CDC;
        final VaccineBrand VACCINE_BRAND = VaccineBrand.JANSSEN;

        Employee employee = new Employee();
        employee.setId(42L);
        employee.setAgencyCode("Delivery");
        employee.setAgencyName("USA");
        employee.setEmail("fry@planetexpress.ny");

        VaccineCardDocument vaccineCardDocument = new VaccineCardDocument();
        vaccineCardDocument.setSubmittedBy("Philip J. Fry");
        vaccineCardDocument.setVaccineShotNumber(2);
        vaccineCardDocument.setVaccineAdministrationDate(LocalDate.now());
        vaccineCardDocument.setVaccineBrand(VACCINE_BRAND);
        vaccineCardDocument.setProofType(PROOF_TYPE);
        vaccineCardDocument.setEmployee(employee);
        List<VaccineCardDocument> vaccineCardDocuments = new ArrayList<VaccineCardDocument>(){{
                add(vaccineCardDocument); 
         }};

        VaccineCardDocument dbDocument = new VaccineCardDocument();
        dbDocument.setVaccineBrand(VACCINE_BRAND);
        dbDocument.setVaccineAdministrationDate(LocalDate.now());
        dbDocument.setLotNumber("X");

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);
        MultipartFile multipartFileMock = mock(MultipartFile.class);

        when(multipartFileMock.getBytes())
                .thenThrow(new IOException(ERROR));

        TypedQuery<VaccineCardDocument> query = mock(TypedQuery.class);

        when(employeeServiceMock.update(employee))
                .thenReturn(employee);

        // Run test
        ResponseEntity<String> response = objectUnderTest_attestationController
                .vaccineDocumentUpload(authenticationMock, employee, vaccineCardDocuments, multipartFileMock);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when file parsing fails");
        assertEquals(
                ERROR,
                response.getBody(),
                "When image processing fails, response body should be the exception message");
    }

    /**
     * TODO: This comment applies to all methods below this point:
     *       Is `covidTestResultUpload` dead code, since policy is no exceptions for weekly testing?
     */

    /**
     * This "covidTestResultUpload(Authentication, Employee, CovidTestResultDocument, MultipartFile)" test is a happy
     * path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 202 response code
     *     - Has a null response body
     *
     * The following interactions are verified, in addition to the return value:
     *     - employeeServiceMock.update should be called once
     *     - processServiceMock.startProcess should be called once
     *     - These following MultipartFile fields should be accessed once each: size, content type, original filename
     */
    @Test
    public void test_covidTestResultUpload_success() {
        // Set up data

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        Employee employee = mock(Employee.class);
        CovidTestResultDocument covidTestResultDocument = mock(CovidTestResultDocument.class);

        // Run test
        ResponseEntity<String> response = objectUnderTest_attestationController
                .covidTestResultUpload(authenticationMock, employee, covidTestResultDocument, multipartFileMock);

        // Validate
        assertEquals(
                HttpStatus.ACCEPTED,
                response.getStatusCode(),
                "Should return HTTP 202 when upload is successful");

        verify(employeeServiceMock, times(1)).update(employee);
        verify(multipartFileMock, times(1)).getOriginalFilename();
        verify(multipartFileMock, times(1)).getContentType();
        verify(multipartFileMock, times(1)).getSize();

        verify(processServiceMock, times(1))
                .startProcess(eq(KJAR_DEPLOYMENT_ID), eq(COVID_TEST_RESULT_SUBMISSION_WORKFLOW),
                        eq(Collections.singletonMap("document",covidTestResultDocument)));

        // TODO: How to override @Value s3PersistenceEnabled, so we can verify the s3Service is invoked?
        //verify(s3ServiceMock, times(1)).put(any());
    }


    /**
     * This "covidTestResultUpload(Authentication, Employee, CovidTestResultDocument, MultipartFile)" test is a negative
     * path validation, for the following condition(s):
     *     - Token validation fails
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - attestationValidatorMock.validateToken returns a non-null (i.e., error) String
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Has an error message indicating the failure case as the response body
     */
    @Test
    public void test_covidTestResultUpload_fail_invalidToken() {
        // Set up data
        final String TOKEN_ERROR_MESSAGE = "Invalid token!";

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        Employee employee = mock(Employee.class);
        CovidTestResultDocument covidTestResultDocument = mock(CovidTestResultDocument.class);

        when(attestationValidatorMock.validateToken(authenticationMock, employee, covidTestResultDocument))
                .thenReturn(TOKEN_ERROR_MESSAGE);

        // Run test
        ResponseEntity<String> response = objectUnderTest_attestationController
                .covidTestResultUpload(authenticationMock, employee, covidTestResultDocument, multipartFileMock);

        // Validate
        assertEquals(
                HttpStatus.FORBIDDEN,
                response.getStatusCode(),
                "Should return HTTP 403 when last name is mismatched between employee and access token");
        assertEquals(
                TOKEN_ERROR_MESSAGE,
                response.getBody(),
                "Response body should indicate that an error occurred validating the token");
    }

    /**
     * This "covidTestResultUpload(Authentication, Employee, CovidTestResultDocument, MultipartFile)" test is a negative
     * path validation, for the following condition(s):
     *     - Employee validation fails
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - attestationValidatorMock.validateEmployee returns a non-null (i.e., error) String
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Has an error message indicating the failure case as the response body
     */
    @Test
    public void test_covidTestResultUpload_fail_invalidEmployee() {
        // Set up data
        final String EMPLOYEE_ERROR_MESSAGE = "Invalid employee!";

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        Employee employee = mock(Employee.class);
        CovidTestResultDocument covidTestResultDocument = mock(CovidTestResultDocument.class);

        when(attestationValidatorMock.validateEmployee(employee))
                .thenReturn(EMPLOYEE_ERROR_MESSAGE);

        // Run test
        ResponseEntity<String> response = objectUnderTest_attestationController
                .covidTestResultUpload(authenticationMock, employee, covidTestResultDocument, multipartFileMock);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when employee id and/or agency are invalid");
        assertEquals(
                EMPLOYEE_ERROR_MESSAGE,
                response.getBody(),
                "Response body should indicate that an error occurred validating the employee");
    }

    /**
     * This "covidTestResultUpload(Authentication, Employee, CovidTestResultDocument, MultipartFile)" test is a negative
     * path validation, for the following condition(s):
     *     - Document validation fails
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - attestationValidatorMock.validateDocument returns a non-null (i.e., error) String
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Has an error message indicating the failure case as the response body
     */
    @Test
    public void test_covidTestResultUpload_fail_invalidDocument() {
        // Set up data
        final String DOCUMENT_ERROR_MESSAGE = "Invalid document!";

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        Employee employee = mock(Employee.class);
        CovidTestResultDocument covidTestResultDocument = mock(CovidTestResultDocument.class);

        when(attestationValidatorMock.validateDocument(covidTestResultDocument))
                .thenReturn(DOCUMENT_ERROR_MESSAGE);

        // Run test
        ResponseEntity<String> response = objectUnderTest_attestationController
                .covidTestResultUpload(authenticationMock, employee, covidTestResultDocument, multipartFileMock);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when test result is invalid");
        assertEquals(
                DOCUMENT_ERROR_MESSAGE,
                response.getBody(),
                "Response body should indicate that document validation failed");
    }

    /**
     * This "covidTestResultUpload(Authentication, Employee, CovidTestResultDocument, MultipartFile)" test is a negative
     * path validation, for the following condition(s):
     *     - Image validation fails
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - imageValidatorMock.validateUploadedImage returns a non-null (i.e., error) String
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Has an error message indicating the failure case as the response body
     */
    @Test
    public void test_covidTestResultUpload_fail_imageValidationFailure() {
        // Set up data
        final String IMAGE_ERROR_MESSAGE = "Invalid image!";

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        Employee employee = mock(Employee.class);
        CovidTestResultDocument covidTestResultDocument = mock(CovidTestResultDocument.class);

        when(imageValidatorMock.validateUploadedImage(multipartFileMock))
                .thenReturn(IMAGE_ERROR_MESSAGE);

        // Run test
        ResponseEntity<String> response = objectUnderTest_attestationController
                .covidTestResultUpload(authenticationMock, employee, covidTestResultDocument, multipartFileMock);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when test result is invalid");
        assertEquals(
                IMAGE_ERROR_MESSAGE,
                response.getBody(),
                "Response body should indicate that document validation failed");
    }

    /**
     * Test that consent date is being set to submission date if the country requires it
     * Currently Australia (AUS) is the only country requiring consent date.
     */
    @Test
    public void consentDateIndiaTest() {
        AttestationController ac = new AttestationController();
        Employee emp = new Employee();
        VaccineCardDocument document = new VaccineCardDocument();
        emp.setAgencyName("IND");
        document.setEmployee(emp);
        document.setProofType(ProofType.DIVOC);
        LocalDateTime submissionDate = LocalDateTime.now();
        document.setSubmissionDate(submissionDate);
        ac.populateEmployeeDetails(document,emp);
        assertEquals(emp.getVaxConsentDate(), document.getSubmissionDate());
    }

    @Test
    public void consentDateAustraliaTest() {
        AttestationController ac = new AttestationController();
        Employee emp = new Employee();
        VaccineCardDocument document = new VaccineCardDocument();
        emp.setAgencyName("AUS");
        document.setEmployee(emp);
        document.setProofType(ProofType.DIVOC);
        LocalDateTime submissionDate = LocalDateTime.now();
        document.setSubmissionDate(submissionDate);
        ac.populateEmployeeDetails(document,emp);
        assertEquals(emp.getVaxConsentDate(), document.getSubmissionDate());
    }

    @Test
    public void consentDateNotNull() {
        AttestationController ac = new AttestationController();
        Employee emp = new Employee();
        VaccineCardDocument document = new VaccineCardDocument();
        emp.setAgencyName("USA");
        document.setEmployee(emp);
        document.setProofType(ProofType.DIVOC);
        LocalDateTime submissionDate = LocalDateTime.now();
        document.setSubmissionDate(submissionDate);
        ac.populateEmployeeDetails(document,emp);
        assertNotEquals(emp.getVaxConsentDate(), null);
    }
}
