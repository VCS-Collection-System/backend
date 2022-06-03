package com.redhat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import com.redhat.vcs.model.CovidTestResultDocument;
import com.redhat.vcs.model.DocumentReviewOutcome;
import com.redhat.vcs.model.DocumentTaskMapping;
import com.redhat.vcs.model.Employee;
import com.redhat.vcs.model.PositiveTestResult;
import com.redhat.vcs.model.VaccineCardDocument;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

/**
 * This class contains the unit (logic / flow) tests for QueryController.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
@ExtendWith(MockitoExtension.class)
public class QueryControllerTest {
    @Mock
    private CustomQueryService queryServiceMock;

    @Mock
    private KeycloakAuthUtil authUtilMock;

    @InjectMocks
    private QueryController objectUnderTest_queryController;

    /**
     * This "getEmployee(Authentication, String)" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.getAgencyName will return a String, whose value is used in subsequent mock behavior
     *     - queryServiceMock.getEmployee will return a mock employee
     *
     * Calling the method under test should return a ResponseEntity<Employee> which:
     *     - Contains a 200 response code
     *     - Contains the mock employee entity as the response body
     */
    @Test
    public void test_getEmployee_success() {
        // Set up data
        final String ID = "1";
        final String AGENCY_NAME = "The Avengers";

        // Set up mocks
        Employee employeeMock = mock(Employee.class);
        Authentication authenticationMock = mock(Authentication.class);

        when(authUtilMock.getAgencyName(authenticationMock))
                .thenReturn(AGENCY_NAME);
        when(queryServiceMock.getEmployee(ID, AGENCY_NAME))
                .thenReturn(employeeMock);

        // Run test
        ResponseEntity<Employee> employeeResponse = objectUnderTest_queryController.getEmployee(authenticationMock, ID);

        // Validation
        assertEquals(
                HttpStatus.OK,
                employeeResponse.getStatusCode(),
                "Should return HTTP 200 on getEmployee success");
        assertSame(
                employeeMock,
                employeeResponse.getBody(),
                "The response body should be the retrieved employee");
    }

    /**
     * This "getEmployee(Authentication, String)" test is a negative path validation, for the following condition(s):
     *     - Employee cannot be found
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.getAgencyName will return a String, whose value is used in subsequent mock behavior
     *     - queryServiceMock.getEmployee will throw `javax.persistence.NoResultException`
     *
     * Calling the method under test should return a ResponseEntity<Employee> which:
     *     - Contains a 404 response code
     *     - Has a null response body
     */
    @Test
    public void test_getEmployee_fail_employeeNotFound() {
        // Set up data
        final String ID = "1";
        final String AGENCY_NAME = "The Avengers";

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);

        when(authUtilMock.getAgencyName(authenticationMock)).thenReturn(AGENCY_NAME);
        when(queryServiceMock.getEmployee(ID, AGENCY_NAME)).thenThrow(new NoResultException("This exception must be thrown for this test to be passed"));

        // Run test
        ResponseEntity<Employee> employeeResponse = objectUnderTest_queryController.getEmployee(authenticationMock, ID);

        // Validation
        assertEquals(
                HttpStatus.NOT_FOUND,
                employeeResponse.getStatusCode(),
                "Should return HTTP 404 when employee is not found");
        assertNull(
                employeeResponse.getBody(),
                "Response body should be null when employee not found");
    }

    /**
     * This "getAcceptedDocuments(Authentication, String)" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.getAgencyName will return a String, whose value is used in subsequent mock behavior
     *     - queryServiceMock.getCovidTestResult will return a List of mock CovidTestResultDocument
     *     - queryServiceMock.getVaccineDocument will return a List of mock VaccineCardDocument
     *
     * Calling the method under test should return a ResponseEntity<Map<String, Object>> which:
     *     - Contains a 200 response code
     *     - Has a Map as the response body, which contains both lists of mocks (created above) as values
     */
    @Test
    public void test_getAcceptedDocuments_success() {
        // Set up data
        final String ID = "1";
        final String AGENCY_NAME = "The Avengers";

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);

        CovidTestResultDocument mockTestResult1 = mock(CovidTestResultDocument.class);
        CovidTestResultDocument mockTestResult2 = mock(CovidTestResultDocument.class);

        List<CovidTestResultDocument> mockTestResultDocs = Arrays.asList(mockTestResult1, mockTestResult2);

        VaccineCardDocument mockVaxCard1 = mock(VaccineCardDocument.class);
        List<VaccineCardDocument> mockVaxCardDocs = Collections.singletonList(mockVaxCard1);

        when(authUtilMock.getAgencyName(authenticationMock))
                .thenReturn(AGENCY_NAME);
        when(queryServiceMock.getCovidTestResult(ID, AGENCY_NAME))
                .thenReturn(mockTestResultDocs);
        when(queryServiceMock.getVaccineDocument(ID, DocumentReviewOutcome.ACCEPTED, AGENCY_NAME))
                .thenReturn(mockVaxCardDocs);

        // Run test
        ResponseEntity<Map<String, Object>> employeeResponse
                = objectUnderTest_queryController.getAcceptedDocuments(authenticationMock, ID);

        // Validation
        assertEquals(
                HttpStatus.OK,
                employeeResponse.getStatusCode(),
                "Should return HTTP 200 on getAcceptedDocuments success");

        assertNotNull(
                employeeResponse.getBody(),
                "Body should not be null on success");

        assertTrue(
                employeeResponse.getBody().containsKey("vaccineDocuments"),
                "Body (Map) should always contain key 'vaccineDocuments', even if associated value is empty");
        assertSame(
                mockVaxCardDocs,
                employeeResponse.getBody().get("vaccineDocuments"),
                "'vaccineDocuments' should contain List from queryService.getVaccineDocument");

        assertTrue(
                employeeResponse.getBody().containsKey("testResultDocuments"),
                "Body (Map) should always contain key 'testResultDocuments', even if associated value is empty");
        assertSame(
                mockTestResultDocs,
                employeeResponse.getBody().get("testResultDocuments"),
                "'testResultDocuments' should contain List from queryService.getCovidTestResult");
    }

    /**
     * This "getTestResultDocuments(Authentication, String)" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.getAgencyName will return a String, whose value is used in subsequent mock behavior
     *     - queryServiceMock.getCovidTestResult will return a List of mock CovidTestResultDocument
     *
     * Calling the method under test should return a ResponseEntity<List<CovidTestResultDocument>> which:
     *     - Contains a 200 response code
     *     - Contains the List of mock test results as the response body
     */
    @Test
    public void test_getTestResultDocuments_success() {
        // Set up data
        final String ID = "1";
        final String AGENCY_NAME = "The Avengers";

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);

        CovidTestResultDocument mockTestResult1 = mock(CovidTestResultDocument.class);
        CovidTestResultDocument mockTestResult2 = mock(CovidTestResultDocument.class);

        List<CovidTestResultDocument> mockTestResultDocs = Arrays.asList(mockTestResult1, mockTestResult2);

        when(authUtilMock.getAgencyName(authenticationMock))
                .thenReturn(AGENCY_NAME);
        when(queryServiceMock.getCovidTestResult(ID, AGENCY_NAME))
                .thenReturn(mockTestResultDocs);

        // Run test
        ResponseEntity<List<CovidTestResultDocument>> employeeResponse
                = objectUnderTest_queryController.getTestResultDocuments(authenticationMock, ID);

        // Validation
        assertEquals(
                HttpStatus.OK,
                employeeResponse.getStatusCode(),
                "Should return HTTP 200 on getTestResultsDocuments success");

        assertSame(
                mockTestResultDocs,
                employeeResponse.getBody(),
                "Response body should be the list of result documents");
    }

    /**
     * This "getVaxDocuments(Authentication, String)" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.getAgencyName will return a String, whose value is used in subsequent mock behavior
     *     - queryServiceMock.getVaccineDocument will return a List of mock VaccineCardDocument
     *
     * Calling the method under test should return a ResponseEntity<List<VaccineCardDocument>> which:
     *     - Contains a 200 response code
     *     - Contains the List of mock vaccine card documents as the response body
     */
    @Test
    public void test_getVaxDocuments_success() {
        // Set up data
        final String ID = "1";
        final String AGENCY_NAME = "The Avengers";

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);

        VaccineCardDocument mockVaxCard1 = mock(VaccineCardDocument.class);
        List<VaccineCardDocument> mockVaxCardDocs = Collections.singletonList(mockVaxCard1);

        when(authUtilMock.getAgencyName(authenticationMock)).thenReturn(AGENCY_NAME);
        when(queryServiceMock.getVaccineDocument(ID, AGENCY_NAME)).thenReturn(mockVaxCardDocs);

        // Run test
        ResponseEntity<List<VaccineCardDocument>> employeeResponse
                = objectUnderTest_queryController.getVaxDocuments(authenticationMock, ID);

        // Validation
        assertEquals(
                HttpStatus.OK,
                employeeResponse.getStatusCode(),
                "Should return HTTP 200 on getVaxDocuments success");

        assertSame(
                mockVaxCardDocs,
                employeeResponse.getBody(),
                "Response body should be the list of vaccine documents");
    }

    /**
     * This "getVaxDocumentsByStatus(Authentication, String, DocumentReviewOutcome)" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.getAgencyName will return a String, whose value is used in subsequent mock behavior
     *     - queryServiceMock.getVaccineDocument will return a List of mock VaccineCardDocument
     *
     * Calling the method under test should return a ResponseEntity<List<VaccineCardDocument>> which:
     *     - Contains a 200 response code
     *     - Contains the List of mock vaccine card documents as the response body
     */
    @Test
    public void test_getVaxDocumentsByStatus_success() {
        // Set up data
        final String ID = "1";
        final String AGENCY_NAME = "The Avengers";
        final DocumentReviewOutcome STATUS = DocumentReviewOutcome.INCONCLUSIVE;

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);

        VaccineCardDocument mockVaxCard1 = mock(VaccineCardDocument.class);
        List<VaccineCardDocument> mockVaxCardDocs = Collections.singletonList(mockVaxCard1);

        when(authUtilMock.getAgencyName(authenticationMock))
                .thenReturn(AGENCY_NAME);
        when(queryServiceMock.getVaccineDocument(ID, STATUS, AGENCY_NAME))
                .thenReturn(mockVaxCardDocs);

        // Run test
        ResponseEntity<List<VaccineCardDocument>> employeeResponse
                = objectUnderTest_queryController.getVaxDocumentsByStatus(authenticationMock, ID, STATUS);

        // Validation
        assertEquals(
                HttpStatus.OK,
                employeeResponse.getStatusCode(),
                "Should return HTTP 200 on getVaxDocumentsByStatus success");

        assertSame(
                mockVaxCardDocs,
                employeeResponse.getBody(),
                "Response body should be the list of vaccine documents");
    }

    /**
     * This "getPositiveResults(Authentication)" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.getAgencyName will return a String, whose value is used in subsequent mock behavior
     *     - queryServiceMock.getVaccineDocument will return a List of mock PositiveTestResult
     *
     * Calling the method under test should return a ResponseEntity<List<PositiveTestResult>> which:
     *     - Contains a 200 response code
     *     - Contains the List of mock positive test results as the response body
     */
    @Test
    public void test_getPositiveResults_success() {
        // Set up data
        final String AGENCY_NAME = "The Avengers";

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);

        PositiveTestResult mockPositiveTestResult1 = mock(PositiveTestResult.class);
        PositiveTestResult mockPositiveTestResult2 = mock(PositiveTestResult.class);
        PositiveTestResult mockPositiveTestResult3 = mock(PositiveTestResult.class);

        List<PositiveTestResult> mockPositiveTestResults = Arrays.asList(
                mockPositiveTestResult1, mockPositiveTestResult2, mockPositiveTestResult3
        );

        when(authUtilMock.getAgencyName(authenticationMock))
                .thenReturn(AGENCY_NAME);
        when(queryServiceMock.getPositiveTestResultByAgency(AGENCY_NAME))
                .thenReturn(mockPositiveTestResults);

        // Run test
        ResponseEntity<List<PositiveTestResult>> employeeResponse
                = objectUnderTest_queryController.getPositiveResults(authenticationMock);

        // Validation
        assertEquals(
                HttpStatus.OK,
                employeeResponse.getStatusCode(),
                "Should return HTTP 200 on getPositiveResults success");

        assertSame(
                mockPositiveTestResults,
                employeeResponse.getBody(),
                "Response body should be the list of positive test results");
    }

    /**
     * This "deletePositiveResultByAgency(Long)" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * The method under test has no return value.
     *
     * The following interactions are verified, in lieu of a return value:
     *     - queryServiceMock.deleteCovidPositiveTestResult should be called once
     */
    @Test
    public void test_deletePositiveResultByAgency_success() {
        // Set up data
        final Long ID = 4L;

        // Set up mocks

        // Run test
        objectUnderTest_queryController.deletePositiveResultByAgency(ID);

        // Validation
        verify(queryServiceMock, times(1)).deleteCovidPositiveTestResult(ID);
    }

    /**
     * This "getDocumentTaskMapping(Authentication, String)" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - queryServiceMock.getDocumentTaskMapping will return a mock document task mapping
     *
     * Calling the method under test should return a ResponseEntity<DocumentTaskMapping> which:
     *     - Contains a 200 response code
     *     - Contains the mock document task mapping as the response body
     */
    @Test
    public void test_getDocumentTaskMapping_success() {
        // Set up data
        final Long DOC_ID = 1985L;

        // Set up mocks
        DocumentTaskMapping documentTaskMappingMock = mock(DocumentTaskMapping.class);
        Authentication authenticationMock = mock(Authentication.class);

        when(queryServiceMock.getDocumentTaskMapping(DOC_ID)).thenReturn(documentTaskMappingMock);

        // Run test
        ResponseEntity<DocumentTaskMapping> documentTaskMappingResponse = objectUnderTest_queryController
                .getDocumentTaskMapping(authenticationMock, DOC_ID);

        // Validation
        assertEquals(
                HttpStatus.OK,
                documentTaskMappingResponse.getStatusCode(),
                "Should return HTTP 200 on getDocumentTaskMapping success");
        assertSame(
                documentTaskMappingMock,
                documentTaskMappingResponse.getBody(),
                "Response body should be the document task mapping");
    }

    /**
     * This "getDocumentTaskMapping(Authentication, Long)" test is a negative path validation, for the following
     * condition(s):
     *     - Document task mapping not found
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - queryServiceMock.getDocumentTaskMapping will throw `javax.persistence.NoResultException`
     *
     * Calling the method under test should return a ResponseEntity<DocumentTaskMapping> which:
     *     - Contains a 404 response code
     *     - Has a null response body
     */
    @Test
    public void test_getDocumentTaskMapping_fail_mappingNotFound() {
        // Set up data
        final Long DOC_ID = 1985L;

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);

        when(queryServiceMock.getDocumentTaskMapping(DOC_ID)).thenThrow(new NoResultException("This exception must be thrown for this test to be passed"));

        // Run test
        ResponseEntity<DocumentTaskMapping> documentTaskMappingResponse = objectUnderTest_queryController
                .getDocumentTaskMapping(authenticationMock, DOC_ID);

        // Validation
        assertEquals(
                HttpStatus.NOT_FOUND,
                documentTaskMappingResponse.getStatusCode(),
                "Should return HTTP 404 when mapping is not found");
        assertNull(
                documentTaskMappingResponse.getBody(),
                "Response body should be null on failure");
    }
}
