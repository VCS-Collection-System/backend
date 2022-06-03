package com.redhat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import com.redhat.vcs.model.CountryConfigurations;
import com.redhat.vcs.model.CovidTestResultDocument;
import com.redhat.vcs.model.DocumentReviewOutcome;
import com.redhat.vcs.model.DocumentTaskMapping;
import com.redhat.vcs.model.Employee;
import com.redhat.vcs.model.PositiveTestResult;
import com.redhat.vcs.model.VaccineCardDocument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/query")
public class QueryController {

    @Autowired
    private CustomQueryService queryService;

    @Autowired
    KeycloakAuthUtil authUtil;

    @GetMapping("employee/{id}")
    public ResponseEntity<Employee> getEmployee(Authentication authentication, @PathVariable String id) {
        String agencyName = authUtil.getAgencyName(authentication);

        if(authUtil.isSuperUser(authentication)){
            try {
                return ResponseEntity.ok(queryService.getEmployee(id));
            } catch(NoResultException e) {
                return ResponseEntity.notFound().build();
            }
        }else{
            try {
                return ResponseEntity.ok(queryService.getEmployee(id, agencyName));
            } catch(NoResultException e) {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @GetMapping("document/{employeeId}/accepted")
    public ResponseEntity<Map<String, Object>> getAcceptedDocuments(
            Authentication authentication,
            @PathVariable String employeeId) {
        String agencyName = authUtil.getAgencyName(authentication);
        List<CovidTestResultDocument> testResultDocs;
        List<VaccineCardDocument> vaxDocs;

        if(authUtil.isSuperUser(authentication) ){
            testResultDocs = queryService.getCovidTestResult(employeeId);
            vaxDocs = queryService.getVaccineDocument(employeeId, DocumentReviewOutcome.ACCEPTED);
        }else{
            testResultDocs = queryService.getCovidTestResult(employeeId, agencyName);
            vaxDocs = queryService.getVaccineDocument(employeeId, DocumentReviewOutcome.ACCEPTED, agencyName);
        }

        Map<String, Object> r = new HashMap<>();
        r.put("testResultDocuments", testResultDocs);
        r.put("vaccineDocuments", vaxDocs);

        return ResponseEntity.ok(r);
    }

    @GetMapping("test-result-document/{employeeId}")
    public ResponseEntity<List<CovidTestResultDocument>> getTestResultDocuments(
            Authentication authentication,
            @PathVariable String employeeId) {
        
        if(authUtil.isSuperUser(authentication) ){
            return ResponseEntity.ok(queryService.getCovidTestResult(employeeId));
        } else{
            String agencyName = authUtil.getAgencyName(authentication);
            return ResponseEntity.ok(queryService.getCovidTestResult(employeeId, agencyName));
        }
    }

    @GetMapping("vax-document/{employeeId}")
    public ResponseEntity<List<VaccineCardDocument>> getVaxDocuments(
            Authentication authentication,
            @PathVariable String employeeId) {
        
        if(authUtil.isSuperUser(authentication) ){
            return ResponseEntity.ok(queryService.getVaccineDocument(employeeId));
        }else{
            String agencyName = authUtil.getAgencyName(authentication);
            return ResponseEntity.ok(queryService.getVaccineDocument(employeeId, agencyName));
        }
    }

    @GetMapping("vax-document/{employeeId}/{status}")
    public ResponseEntity<List<VaccineCardDocument>> getVaxDocumentsByStatus(
            Authentication authentication,
            @PathVariable String employeeId,
            @PathVariable DocumentReviewOutcome status) {
        
        if(authUtil.isSuperUser(authentication) ){
            return ResponseEntity.ok(queryService.getVaccineDocument(employeeId, status));
        }else{
            String agencyName = authUtil.getAgencyName(authentication);
            return ResponseEntity.ok(queryService.getVaccineDocument(employeeId, status, agencyName));
        }
    }
//This returns positive, recovery submissions
    @GetMapping("priority-results")
    public ResponseEntity<List<PositiveTestResult>> getPositiveResults(Authentication authentication) {
        
        if(authUtil.isSuperUser(authentication) ){
            return ResponseEntity.ok(queryService.getPositiveTestResult());
        }else{
            String agencyName = authUtil.getAgencyName(authentication);
            return ResponseEntity.ok(queryService.getPositiveTestResultByAgency(agencyName));
        }
    }

    // TODO: Bug. Check agency name.
    @DeleteMapping("priority-result/{id}")
    public void deletePositiveResultByAgency(@PathVariable Long id) {
        queryService.deleteCovidPositiveTestResult(id);
    }

    @GetMapping("task/document/{documentId}")
    public ResponseEntity<DocumentTaskMapping> getDocumentTaskMapping(
            Authentication authentication,
            @PathVariable Long documentId) {

        try {
            return ResponseEntity.ok(queryService.getDocumentTaskMapping(documentId));
        } catch(NoResultException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("country-configs/{agencyName}")
public ResponseEntity<CountryConfigurations> getCountryConfigs(Authentication authentication,
    @PathVariable String agencyName) {

        try{
            return ResponseEntity.ok(queryService.getCountryConfigurations(agencyName));
        }
        catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

}
