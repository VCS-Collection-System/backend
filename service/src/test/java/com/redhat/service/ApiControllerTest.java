package com.redhat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.redhat.vcs.model.Employee;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

/**
 * This class contains the unit (logic / flow) tests for ApiController.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
@ExtendWith(MockitoExtension.class)
public class ApiControllerTest {
    @Mock
    private EmployeeService employeeServiceMock;

    @Mock
    private CustomQueryService queryServiceMock;

    @Mock
    private KeycloakAuthUtil authUtilMock;

    @InjectMocks
    private ApiController objectUnderTest_apiController;

    /**
     * This "saveEmployee(Authentication, Employee)" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.hasApiRole will return true
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 200 response code
     *     - Has a null response body
     *
     * The following interactions are verified, in addition to the return value:
     *     - queryServiceMock.persistOrOverwriteEmployee should be called once
     */
    @Test
    public void test_saveEmployee_success() {
        // Set up data
        Employee employee = new Employee();
        employee.setEmployeeId("12345");

        // Set up mocks
        Authentication auth = Mockito.mock(Authentication.class);

        when(authUtilMock.hasApiRole(auth))
                .thenReturn(true);

        // Run test
        ResponseEntity<String> response = objectUnderTest_apiController.saveEmployee(auth, employee);

        // Validate
        assertEquals(
                HttpStatus.OK,
                response.getStatusCode(),
                "Should return HTTP 200 on saveEmployee success");
        assertNull(response.getBody(),"Response body should be null on success");

        verify(queryServiceMock, times(1)).persistOrOverwriteEmployee(employee);
    }

    /**
     * This "saveEmployee(Authentication, Employee)" test is a negative path validation, for the following condition(s):
     *     - No API role (hasApiRole -> false)
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.hasApiRole will return false
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 401 response code
     *     - Has a null response body
     */
    @Test
    public void test_saveEmployee_fail_noApiRole() {
        // Set up data

        // Set up mocks
        Authentication auth = Mockito.mock(Authentication.class);

        when(authUtilMock.hasApiRole(auth))
                .thenReturn(false);

        // Run test
        ResponseEntity<String> response = objectUnderTest_apiController.saveEmployee(auth, null);

        // Validate
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode(),
                "Should return HTTP 401 when authentication fails");
        assertNull(
                response.getBody(),
                "Response body should be null when authentication fails");
    }

    /**
     * This "saveEmployee(Authentication, Employee)" test is a negative path validation, for the following condition(s):
     *     - Method called with `null` employee
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.hasApiRole will return true
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Has a null response body
     */
    @Test
    public void test_saveEmployee_fail_nullEmployee() {
        // Set up data

        // Set up mocks
        Authentication auth = Mockito.mock(Authentication.class);

        when(authUtilMock.hasApiRole(auth))
                .thenReturn(true);

        // Run test
        ResponseEntity<String> response = objectUnderTest_apiController.saveEmployee(auth, null);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when employee is null");
        assertNull(
                response.getBody(),
                "Response body should be null when employee is not provided");
    }

    /**
     * This "saveEmployee(Authentication, Employee)" test is a negative path validation, for the following condition(s):
     *     - Employee does not have an id
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.hasApiRole will return true
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Response body is an error message indicating that employee id is missing
     */
    @Test
    public void test_saveEmployee_fail_employeeIdMissing() {
        // Set up data
        Employee employee = new Employee();
        employee.setEmployeeId("");

        // Set up mocks
        Authentication auth = Mockito.mock(Authentication.class);

        when(authUtilMock.hasApiRole(auth))
                .thenReturn(true);

        // Run test
        ResponseEntity<String> response = objectUnderTest_apiController.saveEmployee(auth, employee);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when employee id is missing");
        assertEquals(
                "Employee id is missing",
                response.getBody(),
                "Response body should indicate when employee id is missing");
    }

    /**
     * This "syncEmployee(Authentication, Employee)" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.hasApiRole will return true
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 200 response code
     *     - Has a null response body
     *
     * The following interactions are verified, in addition to the return value:
     *     - employeeServiceMock.sync should be called once
     */
    @Test
    public void test_syncEmployee_success() {
        // Set up data
        Employee employee = new Employee();
        employee.setEmployeeId("12345");

        // Set up mocks
        Authentication auth = Mockito.mock(Authentication.class);

        when(authUtilMock.hasApiRole(auth))
                .thenReturn(true);

        // Run test
        ResponseEntity<String> response = objectUnderTest_apiController.syncEmployee(auth, employee);

        // Validate
        assertEquals(
                HttpStatus.OK,
                response.getStatusCode(),
                "Should return HTTP 200 on syncEmployee success");
        assertNull(
                response.getBody(),
                "Response body should be null on success");

        verify(employeeServiceMock, times(1)).sync(employee);
    }

    /**
     * This "syncEmployee(Authentication, Employee)" test is a negative path validation, for the following condition(s):
     *     - No API role (hasApiRole -> false)
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.hasApiRole will return false
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 401 response code
     *     - Has a null response body
     */
    @Test
    public void test_syncEmployee_fail_noApiRole() {
        // Set up data

        // Set up mocks
        Authentication auth = Mockito.mock(Authentication.class);

        when(authUtilMock.hasApiRole(auth))
                .thenReturn(false);

        // Run test
        ResponseEntity<String> response = objectUnderTest_apiController.syncEmployee(auth, null);

        // Validate
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode(),
                "Should return HTTP 401 when authentication fails");
        assertNull(
                response.getBody(),
                "Response body should be null when authentication fails");
    }

    /**
     * This "syncEmployee(Authentication, Employee)" test is a negative path validation, for the following condition(s):
     *     - Method called with `null` employee
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.hasApiRole will return true
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Has a null response body
     */
    @Test
    public void test_syncEmployee_fail_nullEmployee() {
        // Set up data

        // Set up mocks
        Authentication auth = Mockito.mock(Authentication.class);

        when(authUtilMock.hasApiRole(auth))
                .thenReturn(true);

        // Run test
        ResponseEntity<String> response = objectUnderTest_apiController.syncEmployee(auth, null);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when employee ID is missing");
        assertNull(
                response.getBody(),
                "Response body should be null when the employee is null");
    }

    /**
     * This "syncEmployee(Authentication, Employee)" test is a negative path validation, for the following condition(s):
     *     - Employee does not have an id
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.hasApiRole will return true
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Response body is an error message indicating that employee id is missing
     */
    @Test
    public void test_syncEmployee_fail_employeeIdMissing() {
        // Set up data
        Employee employee = new Employee();
        employee.setEmployeeId("");

        // Set up mocks
        Authentication auth = Mockito.mock(Authentication.class);

        when(authUtilMock.hasApiRole(auth))
                .thenReturn(true);

        // Run test
        ResponseEntity<String> response = objectUnderTest_apiController.syncEmployee(auth, employee);

        // Validate
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when employee ID is missing");
        assertEquals(
                "Employee id is missing",
                response.getBody(),
                "Response body should indicate when employee id is missing");
    }
}
