package com.redhat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.redhat.vcs.model.CountryConfigurations;
import com.redhat.vcs.model.CovidTestResultDocument;
import com.redhat.vcs.model.DocumentReviewOutcome;
import com.redhat.vcs.model.DocumentTaskMapping;
import com.redhat.vcs.model.Employee;
import com.redhat.vcs.model.PositiveTestResult;
import com.redhat.vcs.model.VaccineCardDocument;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * This class contains the unit (logic / flow) tests for CustomQueryService.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
@ExtendWith(MockitoExtension.class)
public class CustomQueryServiceTest {
    @Mock
    private EntityManager em;

    @InjectMocks
    private CustomQueryService objectUnderTest_customQueryService;

    // Used to capture an Employee that is passed into em.merge, for inspection
    @Captor
    ArgumentCaptor<Employee> employeeCaptor;

    @Captor
    ArgumentCaptor<CountryConfigurations> configCaptor;


    /**
     * This "persistOrOverwriteEmployee(Employee)" test is a happy path validation, for the following condition(s):
     *     - Employee does not already exist in the database
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - em.find will return a null value
     *
     * Calling the method under test should return an Employee which:
     *     - Is the same object as was passed into the method under test
     *
     * The following interactions are verified, in addition to the return value:
     *     - em.persist should be called once
     *     - em.merge should never be called
     */
    @Test
    public void test_persistOrOverwriteEmployee_success_persist() {
        // Set up data
        final String ID = "1337";
        Employee employeeToWrite = new Employee();
        employeeToWrite.setEmployeeId(ID);

        // Set up mocks
        when(em.find(Employee.class, ID))
                .thenReturn(null);

        // Run test
        Employee employeeResult = objectUnderTest_customQueryService.persistOrOverwriteEmployee(employeeToWrite);

        // Validate
        assertSame(
                employeeToWrite,
                employeeResult,
                "When persisting for the first time, input and output Employee should be the same object");

        verify(em, times(1)).persist(employeeToWrite);
        verify(em, never()).merge(any(Employee.class));
    }

    /**
     * This "persistOrOverwriteEmployee(Employee)" test is a happy path validation, for the following condition(s):
     *     - Employee already exists in the database
     *     - Passed in employee matches the employee in the database
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - em.find will return the passed in employee
     *
     * Calling the method under test should return an Employee which:
     *     - Is the same object as was passed into the method under test
     *
     * The following interactions are verified, in addition to the return value:
     *     - em.persist should never be called
     *     - em.merge should never be called
     */
    @Test
    public void test_persistOrOverwriteEmployee_success_noUpdate() {
        // Set up data
        final String ID = "1337";
        Employee employee = new Employee();
        employee.setEmployeeId(ID);

        // Set up mocks
        when(em.find(Employee.class, ID))
                .thenReturn(employee);

        // Run test
        Employee employeeResult = objectUnderTest_customQueryService.persistOrOverwriteEmployee(employee);

        // Validate
        assertSame(
                employee,
                employeeResult,
                "When persisting for the first time, input and output Employee should be the same object");

        verify(em, never()).persist(any(Employee.class));
        verify(em, never()).merge(any(Employee.class));
    }

    /**
     * This "persistOrOverwriteEmployee(Employee)" test is a happy path validation, for the following condition(s):
     *     - Employee already exists in the database
     *     - The following fields are null, due to be ignored in the code, or the VCS workflow:
     *          - alternateEmail
     *          - alternateFirstName
     *          - alternateLastName
     *     - Aside from the ignored fields, all fields in the passed-in Employee are non-null, non-empty values
     *     - All fields in the passed-in Employee are different than those on the mock database employee, except for
     *       `id`, `employeeId`, and the ignored fields
     *
     * NOTE: Since the EntityManger is mocked, and em.merge overrides the output value, this test cannot verify the
     * return value (`output`) in the traditional way; instead, this test will capture the value passed into
     * em.merge and verify that it matches what we are expecting.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - em.find will return the mock database entry for the Employee
     *
     * The following interactions are verified, in addition to the return value:
     *     - em.persist should never be called
     *     - em.merge should be called once
     */
    @Test
    public void test_persistOrOverwriteEmployee_success_merge_allFieldsChanged() {
        // Set up data
        final String EMPLOYEE_ID = "1337";

        Employee employeeToWrite = new Employee();
        employeeToWrite.setId(Long.parseLong(EMPLOYEE_ID));
        employeeToWrite.setEmployeeId(EMPLOYEE_ID);

        employeeToWrite.setFirstName("New FN");
        employeeToWrite.setLastName("New LN");
        employeeToWrite.setEmail("new@example.org");
        employeeToWrite.setAgencyCode("New AC");
        employeeToWrite.setAgencyName("New AN");
        employeeToWrite.setDivisionCode("New DC");
        employeeToWrite.setDivisionName("New DN");
        employeeToWrite.setSupervisor("New S");
        employeeToWrite.setHR(true);
        employeeToWrite.setDateOfBirth(LocalDate.now());
        employeeToWrite.setAlternateEmail(null);
        employeeToWrite.setAltFirstName(null);
        employeeToWrite.setAltLastName(null);

        Employee employeeInDatabase = new Employee();
        employeeInDatabase.setId(Long.parseLong(EMPLOYEE_ID));
        employeeInDatabase.setEmployeeId(EMPLOYEE_ID);

        employeeInDatabase.setFirstName("FN");
        employeeInDatabase.setLastName("LN");
        employeeInDatabase.setEmail("new@example.org");
        employeeInDatabase.setAgencyCode("AC");
        employeeInDatabase.setAgencyName("AN");
        employeeInDatabase.setDivisionCode("DC");
        employeeInDatabase.setDivisionName("DN");
        employeeInDatabase.setSupervisor("S");
        employeeInDatabase.setHR(false);
        employeeInDatabase.setDateOfBirth(LocalDate.EPOCH);
        employeeInDatabase.setAlternateEmail(null);
        employeeInDatabase.setAltFirstName(null);
        employeeInDatabase.setAltLastName(null);

        // Set up mocks
        when(em.find(Employee.class, EMPLOYEE_ID))
                .thenReturn(employeeInDatabase);

        // Run test
        objectUnderTest_customQueryService.persistOrOverwriteEmployee(employeeToWrite);

        // Capture the Employee passed into em.merge:
        verify(em).merge(employeeCaptor.capture());
        Employee mergedEmployee = employeeCaptor.getValue();

        // Validate
        assertEquals(
                employeeToWrite,
                mergedEmployee,
                "When all fields are updated, the merged Employee should have values equal to the input Employee");

        assertNotSame(
                employeeToWrite,
                mergedEmployee,
                "When fields are updated and input/merged Employees are equal, they should still be different objects");

        verify(em, never()).persist(any(Employee.class));
        verify(em, times(1)).merge(any(Employee.class));
    }

    /**
     * This "persistOrOverwriteEmployee(Employee)" test is a happy path validation, for the following condition(s):
     *     - Employee already exists in the database
     *     - The following fields are null, due to be ignored in the code, or the VCS workflow:
     *          - alternateEmail
     *          - alternateFirstName
     *          - alternateLastName
     *     - Various fields for the passed-in Employee and the mock database Employee may or may not contain values,
     *       in order to test the following scenarios:
     *          - Value is empty for passed-in employee, exists on database entry
     *          - Value is empty for database entry, exists on passed-in employee
     *          - Value is empty for both database entry and passed-in employee
     *          - Value exists for both database entry and passed-in employee
     *
     * NOTE: Since the EntityManger is mocked, and em.merge overrides the output value, this test cannot verify the
     * return value (`output`) in the traditional way; instead, this test will capture the value passed into
     * em.merge and verify that it matches what we are expecting.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - em.find will return the mock database entry for the Employee
     *
     * The following interactions are verified, in addition to the return value:
     *     - em.persist should never be called
     *     - em.merge should be called once
     */
    @Test
    public void test_persistOrOverwriteEmployee_success_merge_someFieldsChanged() {
        // Set up data
        // Common fields between old and new entries:
        final String EMPLOYEE_ID = "1337";

        final String OLD_FIRST_NAME = "Diana";
        final String NEW_FIRST_NAME = "Wonder";

        final String OLD_LAST_NAME = "Prince";
        final String NEW_LAST_NAME = "Woman";

        final String OLD_EMAIL = "diana.prince@paradise.island";
        final String NEW_EMAIL = "ww@justice.league";

        Employee employeeToWrite = new Employee();
        employeeToWrite.setId(Long.parseLong(EMPLOYEE_ID));
        employeeToWrite.setEmployeeId(EMPLOYEE_ID);
        employeeToWrite.setHR(false);
        employeeToWrite.setAgencyCode(null);

        Employee employeeInDatabase = new Employee();
        employeeInDatabase.setId(Long.parseLong(EMPLOYEE_ID));
        employeeInDatabase.setEmployeeId(EMPLOYEE_ID);
        employeeInDatabase.setHR(false);
        employeeInDatabase.setAgencyCode(null);

        // Differing fields between old and new entries:
        final LocalDate OLD_DOB = LocalDate.now();
        final String NEW_AGENCY_NAME = "The Justice League";

        employeeInDatabase.setFirstName(OLD_FIRST_NAME);
        employeeToWrite.setFirstName(NEW_FIRST_NAME);

        employeeInDatabase.setLastName(OLD_LAST_NAME);
        employeeToWrite.setLastName(NEW_LAST_NAME);

        employeeInDatabase.setEmail(OLD_EMAIL);
        employeeToWrite.setEmail(NEW_EMAIL);

        employeeInDatabase.setDateOfBirth(OLD_DOB);
        employeeToWrite.setDateOfBirth(null);

        employeeInDatabase.setAgencyName(null);
        employeeToWrite.setAgencyName(NEW_AGENCY_NAME);

        // Set up mocks
        when(em.find(Employee.class, EMPLOYEE_ID))
                .thenReturn(employeeInDatabase);

        // Run test
        objectUnderTest_customQueryService.persistOrOverwriteEmployee(employeeToWrite);

        // Capture the Employee passed into em.merge:
        verify(em).merge(employeeCaptor.capture());
        Employee mergedEmployee = employeeCaptor.getValue();

        // Validate
        assertEquals(
                EMPLOYEE_ID,
                mergedEmployee.getEmployeeId(),
                "Employee Id should not have changed");

        assertEquals(
                NEW_FIRST_NAME,
                mergedEmployee.getFirstName(),
                "Employee First Name should have changed to the new value");

        assertEquals(
                NEW_LAST_NAME,
                mergedEmployee.getLastName(),
                "Employee Last Name should have changed to the new value");

        assertEquals(
                NEW_EMAIL,
                mergedEmployee.getEmail(),
                "Employee email should have changed to the new value");

        assertEquals(
                OLD_DOB,
                mergedEmployee.getDateOfBirth(),
                "Employee DOB should be the old value, since the new one was null");

        assertEquals(
                NEW_AGENCY_NAME,
                mergedEmployee.getAgencyName(),
                "Employee Agency Name should have overwritten the old null value");

        assertNull(
                mergedEmployee.getAgencyCode(),
                "Employee Agency Code should be null, since both old and new values were set to null");

        verify(em, never()).persist(any(Employee.class));
        verify(em, times(1)).merge(any(Employee.class));
    }

    /**
     * This "persistOrOverwriteEmployee(Employee)" test is a negative path validation, for the following condition(s):
     *     - Employee Id is null
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should result in a `RuntimeException` being thrown.
     */
    @Test
    public void test_persistOrOverwriteEmployee_fail_nullEmployeeId() {
        // Set up data
        Employee employee = new Employee();
        employee.setEmployeeId(null);

        // Set up mocks

        // Run test & Validate (one step due to thrown exception)
        assertThrows(RuntimeException.class,
                () -> objectUnderTest_customQueryService.persistOrOverwriteEmployee(employee));
    }

    /**
     * This "persistOrOverwriteEmployee(Employee)" test is a negative path validation, for the following condition(s):
     *     - Employee Id is comprised only of whitespace (i.e., is blank)
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should result in a `RuntimeException` being thrown.
     */
    @Test
    public void test_persistOrOverwriteEmployee_fail_blankEmployeeId() {
        // Set up data
        Employee employee = new Employee();
        employee.setEmployeeId("   ");

        // Set up mocks

        // Run test & Validate (one step due to thrown exception)
        assertThrows(RuntimeException.class,
                () -> objectUnderTest_customQueryService.persistOrOverwriteEmployee(employee));
    }

    /**
     * This "persistOrOverwriteEmployee(Employee)" test is a negative path validation, for the following condition(s):
     *     - NoResultException when looking up employee by id
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - em.find will throw a NoResultException
     *
     * Calling the method under test should result in an `Exception` being thrown.
     */
    @Test
    public void test_persistOrOverwriteEmployee_fail_exceptionOnFind() {
        // Set up data
        final String ID = "007";
        Employee employee = new Employee();
        employee.setEmployeeId(ID);

        // Set up mocks
        when(em.find(Employee.class, ID)).thenThrow(
                new NoResultException("This exception must be thrown for this test to be passed"));

        // Run test & Validate (one step due to thrown exception)
        assertThrows(Exception.class,
                () -> objectUnderTest_customQueryService.persistOrOverwriteEmployee(employee));
    }

    /**
     * This "getDocumentTaskMapping(Long, String)" test is a happy path validation.
     *
     * Since this method relies entirely on a call to the EntityManager, this method will simply verify that expected
     * calls / interactions happen.
     */
    @Test
    public void test_getDocumentTaskMapping_success_docId() {
        // Set up data
        final String QUERY_NAME = "document_task_mapping_by_document_id";
        final Long ID = 1000L;

        // Set up mocks
        DocumentTaskMapping documentTaskMapping = mock(DocumentTaskMapping.class);

        TypedQuery<DocumentTaskMapping> mockQueryBase = mock(TypedQuery.class);
        TypedQuery<DocumentTaskMapping> mockQueryWithDocParam = mock(TypedQuery.class);

        when(em.createNamedQuery(QUERY_NAME, DocumentTaskMapping.class))
                .thenReturn(mockQueryBase);
        when(mockQueryBase.setParameter("documentId", ID))
                .thenReturn(mockQueryWithDocParam);
        when(mockQueryWithDocParam.getSingleResult())
                .thenReturn(documentTaskMapping);

        // Run test
        DocumentTaskMapping result = objectUnderTest_customQueryService.getDocumentTaskMapping(ID);

        // Validate
        assertSame(
                documentTaskMapping,
                result,
                "Mock DocumentTaskMapping was not returned; verify the mock interactions against the source!");

        verify(em, times(1))
                .createNamedQuery(QUERY_NAME, DocumentTaskMapping.class);
    }

    /**
     * This "getDocumentTaskMapping(Long, String)" test is a happy path validation.
     *
     * Since this method relies entirely on a call to the EntityManager, this method will simply verify that expected
     * calls / interactions happen.
     */
    @Test
    public void test_getDocumentTaskMapping_success_docIdAndAgency() {
        // Set up data
        final String QUERY_NAME = "document_task_mapping_by_document_id_and_agency_name";
        final Long ID = 1000L;
        final String AGENCY_NAME = "MI6";

        // Set up mocks
        DocumentTaskMapping documentTaskMapping = mock(DocumentTaskMapping.class);

        TypedQuery<DocumentTaskMapping> mockQueryBase = mock(TypedQuery.class);
        TypedQuery<DocumentTaskMapping> mockQueryWithDocParam = mock(TypedQuery.class);
        TypedQuery<DocumentTaskMapping> mockQueryWithAgencyParam = mock(TypedQuery.class);

        when(em.createNamedQuery(QUERY_NAME, DocumentTaskMapping.class))
                .thenReturn(mockQueryBase);
        when(mockQueryBase.setParameter("documentId", ID))
                .thenReturn(mockQueryWithDocParam);
        when(mockQueryWithDocParam.setParameter("agencyName", AGENCY_NAME))
                .thenReturn(mockQueryWithAgencyParam);
        when(mockQueryWithAgencyParam.getSingleResult())
                .thenReturn(documentTaskMapping);

        // Run test
        DocumentTaskMapping result = objectUnderTest_customQueryService.getDocumentTaskMapping(ID, AGENCY_NAME);

        // Validate
        assertSame(
                documentTaskMapping,
                result,
                "Mock DocumentTaskMapping was not returned; verify the mock interactions against the source!");

        verify(em, times(1))
                .createNamedQuery(QUERY_NAME, DocumentTaskMapping.class);
    }

    /**
     * This "getEmployee(String, String)" test is a happy path validation.
     *
     * Since this method relies entirely on a call to the EntityManager, this method will simply verify that expected
     * calls / interactions happen.
     */
    @Test
    public void test_getEmployee_success_empIdAndAgency() {
        // Set up data
        final String QUERY_NAME = "employee_by_employee_id_and_agency_name";
        final String ID = "007";
        final String AGENCY_NAME = "MI6";

        // Set up mocks
        Employee employee = mock(Employee.class);

        TypedQuery<Employee> mockQueryBase = mock(TypedQuery.class);
        TypedQuery<Employee> mockQueryWithEmpIdParam = mock(TypedQuery.class);
        TypedQuery<Employee> mockQueryWithAgencyParam = mock(TypedQuery.class);

        when(em.createNamedQuery(QUERY_NAME, Employee.class))
                .thenReturn(mockQueryBase);
        when(mockQueryBase.setParameter("employeeId", ID))
                .thenReturn(mockQueryWithEmpIdParam);
        when(mockQueryWithEmpIdParam.setParameter("agencyName", AGENCY_NAME))
                .thenReturn(mockQueryWithAgencyParam);
        when(mockQueryWithAgencyParam.getSingleResult())
                .thenReturn(employee);

        // Run test
        Employee result = objectUnderTest_customQueryService.getEmployee(ID, AGENCY_NAME);

        // Validate
        assertSame(
                employee,
                result,
                "Mock Employee was not returned; verify the mock interactions against the source!");

        verify(em, times(1))
                .createNamedQuery(QUERY_NAME, Employee.class);
    }

    /**
     * This "getCovidTestResult(String, String)" test is a happy path validation.
     *
     * Since this method relies entirely on a call to the EntityManager, this method will simply verify that expected
     * calls / interactions happen.
     */
    @Test
    public void test_getCovidTestResult_success_empIdAndAgency() {
        // Set up data
        final String QUERY_NAME = "covid_test_result_doc_by_employee_id_and_agency_name";
        final String ID = "007";
        final String AGENCY_NAME = "MI6";

        // Set up mocks
        List<CovidTestResultDocument> docList = mock(List.class);

        TypedQuery<CovidTestResultDocument> mockQueryBase = mock(TypedQuery.class);
        TypedQuery<CovidTestResultDocument> mockQueryWithEmpIdParam = mock(TypedQuery.class);
        TypedQuery<CovidTestResultDocument> mockQueryWithAgencyParam = mock(TypedQuery.class);

        when(em.createNamedQuery(QUERY_NAME, CovidTestResultDocument.class))
                .thenReturn(mockQueryBase);
        when(mockQueryBase.setParameter("employeeId", ID))
                .thenReturn(mockQueryWithEmpIdParam);
        when(mockQueryWithEmpIdParam.setParameter("agencyName", AGENCY_NAME))
                .thenReturn(mockQueryWithAgencyParam);
        when(mockQueryWithAgencyParam.getResultList())
                .thenReturn(docList);

        // Run test
        List<CovidTestResultDocument> result = objectUnderTest_customQueryService.getCovidTestResult(ID, AGENCY_NAME);

        // Validate
        assertSame(
                docList,
                result,
                "Mock List<CovidTestResultDocument> was not returned; verify the mock interactions!");

        verify(em, times(1))
                .createNamedQuery(QUERY_NAME, CovidTestResultDocument.class);
    }

    /**
     * This "getVaccineDocument(String, String)" test is a happy path validation.
     *
     * Since this method relies entirely on a call to the EntityManager, this method will simply verify that expected
     * calls / interactions happen.
     */
    @Test
    public void test_getVaccineDocument_success_empIdAndAgency() {
        // Set up data
        final String QUERY_NAME = "vax_doc_by_employee_id_and_agency_name";
        final String ID = "007";
        final String AGENCY_NAME = "MI6";

        // Set up mocks
        List<VaccineCardDocument> docList = mock(List.class);

        TypedQuery<VaccineCardDocument> mockQueryBase = mock(TypedQuery.class);
        TypedQuery<VaccineCardDocument> mockQueryWithEmpIdParam = mock(TypedQuery.class);
        TypedQuery<VaccineCardDocument> mockQueryWithAgencyParam = mock(TypedQuery.class);

        when(em.createNamedQuery(QUERY_NAME, VaccineCardDocument.class))
                .thenReturn(mockQueryBase);
        when(mockQueryBase.setParameter("employeeId", ID))
                .thenReturn(mockQueryWithEmpIdParam);
        when(mockQueryWithEmpIdParam.setParameter("agencyName", AGENCY_NAME))
                .thenReturn(mockQueryWithAgencyParam);
        when(mockQueryWithAgencyParam.getResultList())
                .thenReturn(docList);

        // Run test
        List<VaccineCardDocument> result = objectUnderTest_customQueryService.getVaccineDocument(ID, AGENCY_NAME);

        // Validate
        assertSame(
                docList,
                result,
                "Mock List<VaccineCardDocument> was not returned; verify the mock interactions!");

        verify(em, times(1))
                .createNamedQuery(QUERY_NAME, VaccineCardDocument.class);
    }

    /**
     * This "getVaccineDocument(String, String)" test is a happy path validation.
     *
     * Since this method relies entirely on a call to the EntityManager, this method will simply verify that expected
     * calls / interactions happen.
     */
    @Test
    public void test_getVaccineDocument_success_empIdStatusAndAgency() {
        // Set up data
        final String QUERY_NAME = "vax_doc_by_employee_id_and_status_and_agency_name";
        final String ID = "007";
        final DocumentReviewOutcome STATUS = DocumentReviewOutcome.DECLINED;
        final String AGENCY_NAME = "MI6";

        // Set up mocks
        List<VaccineCardDocument> docList = mock(List.class);

        TypedQuery<VaccineCardDocument> mockQueryBase = mock(TypedQuery.class);
        TypedQuery<VaccineCardDocument> mockQueryWithEmpIdParam = mock(TypedQuery.class);
        TypedQuery<VaccineCardDocument> mockQueryWithStatusParam = mock(TypedQuery.class);
        TypedQuery<VaccineCardDocument> mockQueryWithAgencyParam = mock(TypedQuery.class);

        when(em.createNamedQuery(QUERY_NAME, VaccineCardDocument.class))
                .thenReturn(mockQueryBase);
        when(mockQueryBase.setParameter("employeeId", ID))
                .thenReturn(mockQueryWithEmpIdParam);
        when(mockQueryWithEmpIdParam.setParameter("status", STATUS))
                .thenReturn(mockQueryWithStatusParam);
        when(mockQueryWithStatusParam.setParameter("agencyName", AGENCY_NAME))
                .thenReturn(mockQueryWithAgencyParam);
        when(mockQueryWithAgencyParam.getResultList())
                .thenReturn(docList);

        // Run test
        List<VaccineCardDocument> result = objectUnderTest_customQueryService
                .getVaccineDocument(ID, STATUS, AGENCY_NAME);

        // Validate
        assertSame(
                docList,
                result,
                "Mock List<VaccineCardDocument> was not returned; verify the mock interactions!");

        verify(em, times(1))
                .createNamedQuery(QUERY_NAME, VaccineCardDocument.class);
    }

    /**
     * This "getPositiveTestResultByAgency(String)" test is a happy path validation.
     *
     * Since this method relies entirely on a call to the EntityManager, this method will simply verify that expected
     * calls / interactions happen.
     */
    @Test
    public void test_getPositiveTestResultByAgency_success() {
        // Set up data
        final String QUERY_NAME = "covid_positive_by_agency";
        final String AGENCY_NAME = "MI6";

        // Set up mocks
        List<PositiveTestResult> docList = mock(List.class);

        TypedQuery<PositiveTestResult> mockQueryBase = mock(TypedQuery.class);
        TypedQuery<PositiveTestResult> mockQueryWithAgencyParam = mock(TypedQuery.class);

        when(em.createNamedQuery(QUERY_NAME, PositiveTestResult.class))
                .thenReturn(mockQueryBase);
        when(mockQueryBase.setParameter("agency", AGENCY_NAME))
                .thenReturn(mockQueryWithAgencyParam);
        when(mockQueryWithAgencyParam.getResultList())
                .thenReturn(docList);

        // Run test
        List<PositiveTestResult> result = objectUnderTest_customQueryService.getPositiveTestResultByAgency(AGENCY_NAME);

        // Validate
        assertSame(
                docList,
                result,
                "Mock List<PositiveTestResult> was not returned; verify the mock interactions!");

        verify(em, times(1))
                .createNamedQuery(QUERY_NAME, PositiveTestResult.class);
    }

    /**
     * This "getPositiveTestResultByAgencyName(String)" test is a happy path validation.
     *
     * Since this method relies entirely on a call to the EntityManager, this method will simply verify that expected
     * calls / interactions happen.
     */
    @Test
    public void test_getPositiveTestResultByAgencyName_success() {
        // Set up data
        final String QUERY_NAME = "covid_positive_by_agency_name";
        final String AGENCY_NAME = "MI6";

        // Set up mocks
        List<PositiveTestResult> docList = mock(List.class);

        TypedQuery<PositiveTestResult> mockQueryBase = mock(TypedQuery.class);
        TypedQuery<PositiveTestResult> mockQueryWithAgencyParam = mock(TypedQuery.class);

        when(em.createNamedQuery(QUERY_NAME, PositiveTestResult.class))
                .thenReturn(mockQueryBase);
        when(mockQueryBase.setParameter("agency", AGENCY_NAME))
                .thenReturn(mockQueryWithAgencyParam);
        when(mockQueryWithAgencyParam.getResultList())
                .thenReturn(docList);

        // Run test
        List<PositiveTestResult> result = objectUnderTest_customQueryService
                .getPositiveTestResultByAgencyName(AGENCY_NAME);

        // Validate
        assertSame(
                docList,
                result,
                "Mock List<PositiveTestResult> was not returned; verify the mock interactions!");

        verify(em, times(1))
                .createNamedQuery(QUERY_NAME, PositiveTestResult.class);
    }

    /**
     * This "deleteCovidPositiveTestResult(long)" test is a happy path validation, for the following condition(s):
     *     - PositiveTestResult exists in the database for the provided id
     *
     * Since this method relies entirely on a call to the EntityManager, this method will simply verify that expected
     * calls / interactions happen.
     */
    @Test
    public void test_deleteCovidPositiveTestResult_success_deleted() {
        // Set up data
        final long ID = 7L;

        // Set up mocks
        PositiveTestResult mockTestResult = mock(PositiveTestResult.class);

        when(em.find(PositiveTestResult.class, ID))
                .thenReturn(mockTestResult);

        // Run test
        objectUnderTest_customQueryService.deleteCovidPositiveTestResult(ID);

        // Validate
        verify(em, times(1))
                .remove(mockTestResult);
    }

    /**
     * This "deleteCovidPositiveTestResult(long)" test is a happy path validation, for the following condition(s):
     *     - PositiveTestResult exists in the database for the provided id
     *
     * Since this method relies entirely on a call to the EntityManager, this method will simply verify that expected
     * calls / interactions happen.
     */
    @Test
    public void test_deleteCovidPositiveTestResult_success_notFound() {
        // Set up data
        final long ID = 7L;

        // Set up mocks
        when(em.find(PositiveTestResult.class, ID))
                .thenReturn(null);

        // Run test
        objectUnderTest_customQueryService.deleteCovidPositiveTestResult(ID);

        // Validate
        verify(em, never())
                .remove(any(PositiveTestResult.class));
    }

/**
     * This "deleteVaccineRecordById(long)" test is a happy path validation, for the following condition(s):
     *     - vaccine recordx exists in the database for the provided id
     *
     * Since this method relies entirely on a call to the EntityManager, this method will simply verify that expected
     * calls / interactions happen.
     */
    @Test
    public void test_deleteVaccineRecord_success_notFound() {
        // Set up data
        final long id = 7L;
        final String QUERY_NAME = "remove_vax_doc_by_id";

        // Set up mocks
        TypedQuery<VaccineCardDocument> mockQueryBase = mock(TypedQuery.class);
        TypedQuery<VaccineCardDocument> mockQueryWithAgencyParam = mock(TypedQuery.class);

        when(em.createNamedQuery(QUERY_NAME))
                .thenReturn(mockQueryBase);
        when(mockQueryBase.setParameter("id", id))
                .thenReturn(mockQueryWithAgencyParam);
                
        // Run test
        objectUnderTest_customQueryService.deleteVaccineRecordById(id);

        // Validate
        verify(em, never())
                .remove(any(VaccineCardDocument.class));
    }




    /**
     * This "deleteCovidPositiveTestResult(long)" test is a negative path validation, for the following condition(s):
     *     - Exception thrown on em.find
     *
     * Since this method relies entirely on a call to the EntityManager, this method will simply verify that expected
     * calls / interactions happen.
     */
    @Test
    public void test_deleteCovidPositiveTestResult_fail_exceptionOnFind() {
        // Set up data
        final long ID = 7L;

        // Set up mocks
        when(em.find(PositiveTestResult.class, ID))
                .thenThrow(new NoResultException("This exception must be thrown for this test to be passed"));

        // Run test & Validate (one step due to thrown Exception)
        assertThrows(RuntimeException.class,
                () -> objectUnderTest_customQueryService.deleteCovidPositiveTestResult(ID));

        // Validate
        verify(em, never())
                .remove(any(PositiveTestResult.class));
    }

    @Test
    public void test_persistCountryConfigurations_success_persist() {
        // Set up data
        final String agencyName = "usa";
        CountryConfigurations config = new CountryConfigurations();
        config.setAgencyName(agencyName);

        // Set up mocks
        when(em.find(CountryConfigurations.class, agencyName))
                .thenReturn(null);

        // Run test
        CountryConfigurations configResult = objectUnderTest_customQueryService.persistCountryConfigurations(config);
        
        // Validate
        assertSame(
                config,
                configResult,
                "When persisting for the first time, input and output Employee should be the same object");

        verify(em, times(1)).persist(config);
        verify(em, never()).merge(any(CountryConfigurations.class));
    }

    @Test
    public void test_persistCountryConfigurations_success_noUpdate() {
        // Set up data
        final String agencyName = "usa";
        CountryConfigurations config = new CountryConfigurations();
        config.setAgencyName(agencyName);

        // Set up mocks
        when(em.find(CountryConfigurations.class, agencyName))
                .thenReturn(config);

        // Run test
        CountryConfigurations configResult = objectUnderTest_customQueryService.persistCountryConfigurations(config);
        // Validate
        assertSame(
                config,
                configResult,
                "When persisting for the first time, input and output Config should be the same object");

        verify(em, never()).persist(any(CountryConfigurations.class));
        verify(em, never()).merge(any(CountryConfigurations.class));
    }


    @Test
    public void test_persistCountryConfigurations_fail_nullEmployeeId() {
        // Set up data
        CountryConfigurations config = new CountryConfigurations();
        config.setAgencyName(null);

        // Set up mocks

        // Run test & Validate (one step due to thrown exception)
        assertThrows(RuntimeException.class,
                () -> objectUnderTest_customQueryService.persistCountryConfigurations(config));
    }
}
