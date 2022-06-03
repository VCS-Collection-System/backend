package com.redhat.service.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.redhat.service.KeycloakAuthUtil;
import com.redhat.vcs.model.CovidTestResultDocument;
import com.redhat.vcs.model.Document;
import com.redhat.vcs.model.Employee;
import com.redhat.vcs.model.VaccineCardDocument;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.AccessToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

/**
 * This class contains the unit (logic / flow) tests for AttestationValidator.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
@ExtendWith(MockitoExtension.class)
public class AttestationValidatorTest {
    @Mock
    private KeycloakAuthUtil authUtilMock;

    @InjectMocks
    private AttestationValidator objectUnderTest_attestationValidator;

    /**
     * This "validateToken(Authentication, Employee, Document)" test is a happy path validation, for the following
     * condition(s):
     *     - Document has a non-empty "submitted by" field
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - Document returns a non-empty value for `submittedBy`
     *
     * Calling the method under test should return a null value.
     */
    @Test
    public void test_validateToken_success_submittedBy() {
        // Set up data

        // Set up mocks
        Authentication authentication = mock(Authentication.class);
        Employee employee = mock(Employee.class);
        Document document = mock(Document.class);

        when(document.getSubmittedBy())
                .thenReturn("Philip J. Fry");

        // Run test
        String errorMessage = objectUnderTest_attestationValidator.validateToken(authentication, employee, document);

        // Validate
        assertNull(errorMessage);
    }

    /**
     * This "validateToken(Authentication, Employee, Document)" test is a happy path validation, for the following
     * condition(s):
     *     - Document has a null "submitted by" field
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtil.getAccessToken returns a mock token, matching the values we would expect for a happy path scenario
     *
     * Calling the method under test should return a null value.
     */
    @Test
    public void test_validateToken_success_noSubmittedBy() {
        // Set up data
        final String FIRST_NAME = "Beowulf";
        final String LAST_NAME = "Son of Ecgtheow";

        // Set up mocks
        Authentication authentication = mock(Authentication.class);
        Employee employee = mock(Employee.class);
        Document document = mock(Document.class);
        AccessToken accessToken = mock(AccessToken.class);

        when(document.getSubmittedBy())
                .thenReturn(null);

        when(employee.getFirstName())
                .thenReturn(FIRST_NAME);
        when(employee.getLastName())
                .thenReturn(LAST_NAME);

        when(accessToken.getGivenName())
                .thenReturn(FIRST_NAME);
        when(accessToken.getFamilyName())
                .thenReturn(LAST_NAME);

        when(authUtilMock.getAccessToken(authentication))
                .thenReturn(accessToken);

        // Run test
        String errorMessage = objectUnderTest_attestationValidator.validateToken(authentication, employee, document);

        // Validate
        assertNull(errorMessage);
    }

    /**
     * This "validateToken(Authentication, Employee, Document)" test is a negative path validation, for the following
     * condition(s):
     *     - The first name on AccessToken and Employee do not match
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.getAccessToken returns a test/dummy AccessToken
     *
     * Calling the method under test should return a String which:
     *     - Indicates the failure case (invalid first name)
     */
    @Test
    public void test_validateToken_fail_invalidFirstName() {
        // Set up data
        final String FIRST_NAME = "Beowulf";
        final String LAST_NAME = "Son of Ecgtheow";
        final String BAD_FIRST_NAME = "Frodo";

        AccessToken accessToken = new AccessToken();
        accessToken.setGivenName(FIRST_NAME);
        accessToken.setFamilyName(LAST_NAME);

        Employee employee = new Employee();
        employee.setFirstName(BAD_FIRST_NAME);
        employee.setLastName(LAST_NAME);

        VaccineCardDocument vaccineCardDocument = new VaccineCardDocument();
        vaccineCardDocument.setSubmittedBy(" ");

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);

        when(authUtilMock.getAccessToken(authenticationMock))
                .thenReturn(accessToken);

        // Run test
        String error = objectUnderTest_attestationValidator
                .validateToken(authenticationMock, employee, vaccineCardDocument);

        // Validate
        assertEquals(
                "The employee first name in the payload '" + BAD_FIRST_NAME
                        + "' does not match the one retrieved from sso token '" + FIRST_NAME + "'",
                error,
                "The error text should indicate that the first name is invalid");
    }

    /**
     * This "validateToken(Authentication, Employee, Document)" test is a negative path validation, for the following
     * condition(s):
     *     - The last name on AccessToken and Employee do not match
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.getAccessToken returns a test/dummy AccessToken
     *
     * Calling the method under test should return a String which:
     *     - Indicates the failure case (invalid last name)
     */
    @Test
    public void test_validateToken_fail_invalidLastName() {
        // Set up data
        final String FIRST_NAME = "Beowulf";
        final String LAST_NAME = "Son of Ecgtheow";
        final String BAD_LAST_NAME = "Baggins";

        AccessToken accessToken = new AccessToken();
        accessToken.setGivenName(FIRST_NAME);
        accessToken.setFamilyName(LAST_NAME);

        Employee employee = new Employee();
        employee.setFirstName(FIRST_NAME);
        employee.setLastName(BAD_LAST_NAME);

        CovidTestResultDocument covidTestResultDocument = new CovidTestResultDocument();
        covidTestResultDocument.setSubmittedBy(" ");

        // Set up mocks
        Authentication authenticationMock = mock(Authentication.class);
        MultipartFile multipartFileMock = mock(MultipartFile.class);

        when(authUtilMock.getAccessToken(authenticationMock))
                .thenReturn(accessToken);

        // Run test
        String error = objectUnderTest_attestationValidator
                .validateToken(authenticationMock, employee, covidTestResultDocument);

        // Validate
        assertEquals(
                "The employee last name in the payload '" + BAD_LAST_NAME
                        + "' does not match the one retrieved from sso token '" + LAST_NAME + "'",
                error,
                "The error text should indicate that the last name is invalid");
    }


    /**
     * This "validateEmployee(Employee)" test is a negative path validation, for the following condition(s):
     *     - employee.validate returns a non-empty List
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - authUtilMock.getAccessToken returns a test/dummy AccessToken
     *
     * Calling the method under test should return a String which:
     *     - Indicates the failure case (invalid last name)
     */
    @Test
    public void test_validateEmployee_fail() {
        // Set up data
        final String ERROR_1 = "Invalid first name";
        final String ERROR_2 = "Invalid last name";

        final List<String> ERRORS = new ArrayList<>();
        ERRORS.add(ERROR_1);
        ERRORS.add(ERROR_2);

        // Set up mocks
        Employee employee = mock(Employee.class);
        when(employee.validate())
                .thenReturn(ERRORS);

        // Run test
        String errorMessage = objectUnderTest_attestationValidator.validateEmployee(employee);

        // Validate
        assertNotNull(errorMessage);

        assertEquals(
                "Validation errors ...\n" + ERROR_1 + "\n" + ERROR_2,
                errorMessage,
                "The given error message be an aggregate of all validation errors");
    }
}
