package com.redhat.vcs.model;

import static com.redhat.vcs.model.EmployeeUtil.createEmployee;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

/**
 * This class contains the unit (logic / flow) tests for Employee.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 *
 * Note: Due to the (unenforced) contract between equals and hashCode, both methods will be tested together.
 */
public class EmployeeTest {
    /**
     * This "validate()" test is a happy path validation, for the following condition(s):
     *     - Id is provided, but Employee Id is not
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a List<String> which:
     *     - Should be empty
     *
     * The following side effects are verified, in addition to the return value:
     *     - Employee Id is set to the string value of the original Id
     *     - Id is set to null
     */
    @Test
    public void test_validate_success_blankEmployeeId() {
        // Set up data
        final Long ID = 8675309L;
        final String AGENCY_CODE = "CO";
        final String EMAIL = "jenny@example.org";

        Employee employee = new Employee();

        employee.setId(ID);
        employee.setEmployeeId(null);
        employee.setAgencyCode(AGENCY_CODE);
        employee.setEmail(EMAIL);

        // Set up mocks

        // Run test
        List<String> errors = employee.validate();

        // Validate
        assertTrue(errors.isEmpty(), "There should not be any validation errors when all fields are provided");

        assertEquals(
                Long.toString(ID),
                employee.getEmployeeId(),
                "When no Employee Id is provided, validation method should set it to the value of the Id");

        assertNull(
                employee.getId(),
                "Id should be set to null during validation, if the provided Employee Id is null");
    }

    /**
     * This "validate()" test is a happy path validation, for the following condition(s):
     *     - Employee Id is provided, but Id is not
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a List<String> which:
     *     - Should be empty
     *
     * The following side effects are verified, in addition to the return value:
     *     - Employee Id is set to the string value of the original Id
     *     - Id is set to null
     */
    @Test
    public void test_validate_success_blankId() {
        // Set up data
        final Long ID = 8675309L;
        final String AGENCY_CODE = "CO";
        final String EMAIL = "jenny@example.org";

        Employee employee = new Employee();

        employee.setId(null);
        employee.setEmployeeId(Long.toString(ID));
        employee.setAgencyCode(AGENCY_CODE);
        employee.setEmail(EMAIL);

        // Set up mocks

        // Run test
        List<String> errors = employee.validate();

        // Validate
        assertTrue(errors.isEmpty(), "There should not be any validation errors when all fields are provided");

        assertEquals(
                Long.toString(ID),
                employee.getEmployeeId(),
                "When no Employee Id is provided, validation method should set it to the value of the Id");

        assertNull(
                employee.getId(),
                "Id should be set to null during validation, if the provided Employee Id is null");
    }

    /**
     * This "validate()" test is a negative path validation, for the following condition(s):
     *     - Employee Id (as well as Id) not provided
     *     - Agency code not provided
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a List<String> which:
     *     - Contains exactly two items:
     *         - A String indicating that the employee id is missing
     *         - A String indicating that the employee agency is missing
     */
    @Test
    public void test_validate_fail_missingEmployeeIdAndAgency() {
        // Set up data
        final Long ID = null;
        final String AGENCY_CODE = null;
        final String EMPLOYEE_ID = null;
        final String EMAIL = "fry@planetexpress.ny";

        Employee employee = new Employee();
        employee.setId(ID);
        employee.setEmployeeId(EMPLOYEE_ID);
        employee.setAgencyCode(AGENCY_CODE);
        employee.setEmail(EMAIL);

        List<String> validationErrors = Arrays.asList(
                "Employee id is missing.",
                "Employee agency is missing."
        );

        // Set up mocks

        // Run test
        List<String> errors = employee.validate();

        // Validate
        assertFalse(errors.isEmpty(), "There should not be validation errors when fields are missing");
        assertEquals(
                2,
                errors.size(),
                "Validation should have returned two errors");

        errors.forEach(e -> assertTrue(validationErrors.contains(e), "Validation error missing: " + e));
    }

    /**
     * This "validate()" test is a negative path validation, for the following condition(s):
     *     - Employee Id (as well as Id) not provided
     *     - Agency code not provided
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a List<String> which:
     *     - Contains exactly two items:
     *         - A String indicating that the employee id is missing
     *         - A String indicating that the employee agency is missing
     */
    @Test
    public void test_validate_fail_missingEmail() {
        // Set up data
        final String AGENCY_CODE = "MI6";
        final String EMPLOYEE_ID = "007";
        final String EMAIL = null;

        Employee employee = new Employee();
        employee.setEmployeeId(EMPLOYEE_ID);
        employee.setAgencyCode(AGENCY_CODE);
        employee.setEmail(EMAIL);

        List<String> validationErrors = Collections.singletonList("Both email and alternate email are missing");

        // Set up mocks

        // Run test
        List<String> errors = employee.validate();

        // Validate
        assertFalse(errors.isEmpty(), "There should not be validation errors when fields are missing");
        assertEquals(
                1,
                errors.size(),
                "Validation should have returned one error");

        assertTrue(validationErrors.contains(errors.stream().findFirst().get()));
    }

    /**
     * This "overwriteLdapFields(Employee)" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should modify the object under test to overwrite ONLY the following fields:
     *     - agencyCode
     *     - agencyName
     *     - divisionCode
     *     - divisionName
     *     - email
     *     - firstName
     *     - hr
     *     - lastName
     *     - supervisor
     */
    @Test
    public void test_overwriteLdapFields_success() {
        // Set up data
        final Long ID = 8675309L;
        final String EMPLOYEE_ID = Long.toString(ID);
        final String AGENCY_CODE = "CO";
        final String AGENCY_NAME = "Columbia";
        final String DIVISION_CODE = "PP";
        final String DIVISION_NAME = "Power Pop";
        final LocalDate DOB = LocalDate.of(1981, 11, 16);
        final String EMAIL = "jenny@example.org";
        final String FIRST_NAME = "Jenny";
        final String LAST_NAME = "Jenni";
        final String SUPERVISOR = "Tommy Tutone";
        final Boolean IS_HR = Boolean.FALSE;

        final String ALT_FIRST_NAME = "Putting a value here, because code coverage";
        final String ALT_LAST_NAME = "Putting a value here too, because code coverage";
        final String ALT_EMAIL = "Putting a value here as well, because code coverage";

        final Long DIFFERENT_ID = 1L;
        final String DIFFERENT_EMPLOYEE_ID = Long.toString(DIFFERENT_ID);
        final String DIFFERENT_AGENCY_CODE = "A";
        final String DIFFERENT_AGENCY_NAME = "ABC";
        final String DIFFERENT_DIVISION_CODE = "Test";
        final String DIFFERENT_DIVISION_NAME = "TestTest";
        final LocalDate DIFFERENT_DOB = LocalDate.of(1999, 12, 31);
        final String DIFFERENT_EMAIL = "fakeemail";
        final String DIFFERENT_FIRST_NAME = "Anony";
        final String DIFFERENT_LAST_NAME = "Mouse";
        final String DIFFERENT_SUPERVISOR = "deadmau5";
        final Boolean DIFFERENT_IS_HR = Boolean.TRUE;

        final String DIFFERENT_ALT_FIRST_NAME = "A";
        final String DIFFERENT_ALT_LAST_NAME = "B";
        final String DIFFERENT_ALT_EMAIL = "C";

        Employee employee = new Employee();

        employee.setId(ID);
        employee.setEmployeeId(EMPLOYEE_ID);
        employee.setAgencyCode(AGENCY_CODE);
        employee.setAgencyName(AGENCY_NAME);
        employee.setDateOfBirth(DOB);
        employee.setDivisionCode(DIVISION_CODE);
        employee.setDivisionName(DIVISION_NAME);
        employee.setFirstName(FIRST_NAME);
        employee.setLastName(LAST_NAME);
        employee.setEmail(EMAIL);
        employee.setHR(IS_HR);
        employee.setSupervisor(SUPERVISOR);

        employee.setAltFirstName(ALT_FIRST_NAME);
        employee.setAltLastName(ALT_LAST_NAME);
        employee.setAlternateEmail(ALT_EMAIL);

        Employee newEmployeeData = new Employee();

        newEmployeeData.setId(DIFFERENT_ID);
        newEmployeeData.setEmployeeId(DIFFERENT_EMPLOYEE_ID);
        newEmployeeData.setAgencyCode(DIFFERENT_AGENCY_CODE);
        newEmployeeData.setAgencyName(DIFFERENT_AGENCY_NAME);
        newEmployeeData.setDateOfBirth(DIFFERENT_DOB);
        newEmployeeData.setDivisionCode(DIFFERENT_DIVISION_CODE);
        newEmployeeData.setDivisionName(DIFFERENT_DIVISION_NAME);
        newEmployeeData.setFirstName(DIFFERENT_FIRST_NAME);
        newEmployeeData.setLastName(DIFFERENT_LAST_NAME);
        newEmployeeData.setEmail(DIFFERENT_EMAIL);
        newEmployeeData.setHR(DIFFERENT_IS_HR);
        newEmployeeData.setSupervisor(DIFFERENT_SUPERVISOR);

        newEmployeeData.setAltFirstName(DIFFERENT_ALT_FIRST_NAME);
        newEmployeeData.setAltLastName(DIFFERENT_ALT_LAST_NAME);
        newEmployeeData.setAlternateEmail(DIFFERENT_ALT_EMAIL);

        // Set up mocks

        // Run test
        employee.overwriteLdapFields(newEmployeeData);

        // Validate
        assertEquals(
                employee.getAgencyCode(),
                newEmployeeData.getAgencyCode(),
                "Field should have been overwritten: AgencyCode");

        assertEquals(
                employee.getAgencyName(),
                newEmployeeData.getAgencyName(),
                "Field should have been overwritten: AgencyName");

        assertEquals(
                employee.getDivisionCode(),
                newEmployeeData.getDivisionCode(),
                "Field should have been overwritten: DivisionCode");

        assertEquals(
                employee.getDivisionName(),
                newEmployeeData.getDivisionName(),
                "Field should have been overwritten: DivisionName");

        assertEquals(
                employee.getEmail(),
                newEmployeeData.getEmail(),
                "Field should have been overwritten: Email");

        assertEquals(
                employee.getFirstName(),
                newEmployeeData.getFirstName(),
                "Field should have been overwritten: FirstName");

        assertEquals(
                employee.getLastName(),
                newEmployeeData.getLastName(),
                "Field should have been overwritten: LastName");

        assertEquals(
                employee.isHR(),
                newEmployeeData.isHR(),
                "Field should have been overwritten: HR");

        assertEquals(
                employee.getSupervisor(),
                newEmployeeData.getSupervisor(),
                "Field should have been overwritten: Supervisor");

        assertNotEquals(
                employee.getDateOfBirth(),
                newEmployeeData.getDateOfBirth(),
                "Field should NOT have been overwritten: DateOfBirth");

        assertNotEquals(
                employee.getAltFirstName(),
                newEmployeeData.getAltFirstName(),
                "Field should NOT have been overwritten: AltFirstName");

        assertNotEquals(
                employee.getAltLastName(),
                newEmployeeData.getAltLastName(),
                "Field should NOT have been overwritten: AltLastName");

        assertNotEquals(
                employee.getAlternateEmail(),
                newEmployeeData.getAlternateEmail(),
                "Field should NOT have been overwritten: AlternateEmail");

        assertNotEquals(
                employee.getId(),
                newEmployeeData.getId(),
                "Field should NOT have been overwritten: Id");

        assertNotEquals(
                employee.getEmployeeId(),
                newEmployeeData.getEmployeeId(),
                "Field should NOT have been overwritten: EmployeeId");

    }

    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the Employee
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final Long ID = 8675309L;
        final String EMPLOYEE_ID = Long.toString(ID);
        final String AGENCY_CODE = "CO";
        final String AGENCY_NAME = "Columbia";
        final String DIVISION_CODE = "PP";
        final String DIVISION_NAME = "Power Pop";
        final LocalDate DOB = LocalDate.of(1981, 11, 16);
        final LocalDateTime CONSENTDATE = LocalDateTime.of(1981, 11, 16, 01, 01, 01);
        final String EMAIL = "jenny@example.org";
        final String FIRST_NAME = "Jenny";
        final String LAST_NAME = "Jenni";
        final String SUPERVISOR = "Tommy Tutone";
        final Boolean IS_HR = Boolean.FALSE;

        Employee employee = new Employee();

        employee.setId(ID);
        employee.setEmployeeId(EMPLOYEE_ID);
        employee.setAgencyCode(AGENCY_CODE);
        employee.setAgencyName(AGENCY_NAME);
        employee.setDateOfBirth(DOB);
        employee.setVaxConsentDate(CONSENTDATE);
        employee.setDivisionCode(DIVISION_CODE);
        employee.setDivisionName(DIVISION_NAME);
        employee.setFirstName(FIRST_NAME);
        employee.setLastName(LAST_NAME);
        employee.setEmail(EMAIL);
        employee.setHR(IS_HR);
        employee.setSupervisor(SUPERVISOR);

        // Not used in the VCS workflow:
        final String ALT_FIRST_NAME = null;
        final String ALT_LAST_NAME = null;
        final String ALT_EMAIL = null;

        employee.setAltFirstName(ALT_FIRST_NAME);
        employee.setAltLastName(ALT_LAST_NAME);
        employee.setAlternateEmail(ALT_EMAIL);

        final String EXPECTED_EMPLOYEE_REGEX = "Employee \\["
                + "id=" + ID
                + ", employeeId=" + "\\*+"
                + ", firstName=" + "\\*+"
                + ", lastName=" + "\\*+"
                + ", email=" + "\\*+"
                + ", agencyCode=" + AGENCY_CODE
                + ", agencyName=" + AGENCY_NAME
                + ", divisionCode=" + DIVISION_CODE
                + ", divisionName=" + DIVISION_NAME
                + ", supervisor=" + "\\*+"
                + ", isHR=" + IS_HR
                + ", dateOfBirth=" + "\\*\\*/\\*\\*/\\*\\*\\*\\*"
                + ", vaxConsentDate=" + CONSENTDATE
                + ", alternateEmail=" + "\\*+"
                + ", altFirstName=" + "\\*+"
                + ", altLastName=" + "\\*+"
                + "\\]";

        // Set up mocks

        // Run test
        String employeeString = employee.toString();

        // Validate
        assertTrue(
                employeeString.matches(EXPECTED_EMPLOYEE_REGEX),
                "Employee String does not match the expected format");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Objects are equal
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a boolean value which:
     *     - Is "true"
     */
    @Test
    public void test_equals_hashcode_success_equivalentObjects() {
        // Set up data
        final Long ID = 8675309L;
        final String EMPLOYEE_ID = Long.toString(ID);
        final String AGENCY_CODE = "CO";
        final String AGENCY_NAME = "Columbia";
        final String DIVISION_CODE = "PP";
        final String DIVISION_NAME = "Power Pop";
        final LocalDate DOB = LocalDate.of(1981, 11, 16);
        final String EMAIL = "jenny@example.org";
        final String FIRST_NAME = "Jenny";
        final String LAST_NAME = "Jenni";
        final String SUPERVISOR = "Tommy Tutone";
        final Boolean IS_HR = Boolean.FALSE;

        Employee employee = new Employee();

        employee.setId(ID);
        employee.setEmployeeId(EMPLOYEE_ID);
        employee.setAgencyCode(AGENCY_CODE);
        employee.setAgencyName(AGENCY_NAME);
        employee.setDateOfBirth(DOB);
        employee.setDivisionCode(DIVISION_CODE);
        employee.setDivisionName(DIVISION_NAME);
        employee.setFirstName(FIRST_NAME);
        employee.setLastName(LAST_NAME);
        employee.setEmail(EMAIL);
        employee.setHR(IS_HR);
        employee.setSupervisor(SUPERVISOR);

        // Not used in the VCS workflow:
        final String ALT_FIRST_NAME = "Putting a value here, because code coverage";
        final String ALT_LAST_NAME = "Putting a value here too, because code coverage";
        final String ALT_EMAIL = "Putting a value here as well, because code coverage";

        employee.setAltFirstName(ALT_FIRST_NAME);
        employee.setAltLastName(ALT_LAST_NAME);
        employee.setAlternateEmail(ALT_EMAIL);

        Employee other = new Employee();

        other.setId(ID);
        other.setEmployeeId(EMPLOYEE_ID);
        other.setAgencyCode(AGENCY_CODE);
        other.setAgencyName(AGENCY_NAME);
        other.setDateOfBirth(DOB);
        other.setDivisionCode(DIVISION_CODE);
        other.setDivisionName(DIVISION_NAME);
        other.setFirstName(FIRST_NAME);
        other.setLastName(LAST_NAME);
        other.setEmail(EMAIL);
        other.setHR(IS_HR);
        other.setSupervisor(SUPERVISOR);

        other.setAltFirstName(ALT_FIRST_NAME);
        other.setAltLastName(ALT_LAST_NAME);
        other.setAlternateEmail(ALT_EMAIL);

        // Set up mocks

        // Run test
        boolean equals = employee.equals(other);

        // Validate
        assertTrue(
                equals,
                "Employees constructed from the same values should be equal");

        assertEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are equal, their hash codes should also be equal");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Both objects are the same reference (i.e., obj1 == obj2)
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a boolean value which:
     *     - Is "true"
     */
    @Test
    public void test_equals_hashcode_success_equal_sameObject() {
        // Set up data
        Employee employee = new Employee();
        Employee other = employee;

        // Set up mocks

        // Run test
        boolean equals = employee.equals(other);

        // Validate
        assertTrue(
                equals,
                "An object should always be equal to itself");

        assertEquals(
                employee.hashCode(),
                other.hashCode(),
                "If an object remains the same, hashCode should continually evaluate to the same value");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Other object is null
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a boolean value which:
     *     - Is "false"
     */
    @Test
    public void test_equals_hashcode_success_notEqual_null() {
        // Set up data
        Employee employee = new Employee();
        Employee other = null;

        // Set up mocks

        // Run test
        boolean equals = employee.equals(other);

        // Validate
        assertFalse(
                equals,
                "The equals method should never return true for a null value");

        assertNotEquals(
                employee.hashCode(),
                // NOTE: Using Objects.hash, since `other` (null) cannot be dereferenced
                Objects.hash(other),
                "When two objects are not equal, their hash codes should also be not equal");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Other object is not an Employee
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a boolean value which:
     *     - Is "false"
     */
    @Test
    public void test_equals_hashcode_success_notEqual_differentClass() {
        // Set up data
        Employee employee = new Employee();
        Long other = 42L;

        // Set up mocks

        // Run test
        boolean equals = employee.equals(other);

        // Validate
        assertFalse(
                equals,
                "Objects of different classes should never be equal");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Objects are the same class, but contain different values
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a boolean value which:
     *     - Is "false"
     *
     * NOTE: In order to get code coverage here a multitude of new methods, this test repeatedly alters the comparison
     *       object and reruns the test.
     */
    @Test
    public void test_equals_hashcode_success_notEqual_differentValues() {
        // Set up data
        final Long ID = 8675309L;
        final String EMPLOYEE_ID = Long.toString(ID);
        final String AGENCY_CODE = "CO";
        final String AGENCY_NAME = "Columbia";
        final String DIVISION_CODE = "PP";
        final String DIVISION_NAME = "Power Pop";
        final LocalDate DOB = LocalDate.of(1981, 11, 16);
        final String EMAIL = "jenny@example.org";
        final String FIRST_NAME = "Jenny";
        final String LAST_NAME = "Jenni";
        final String SUPERVISOR = "Tommy Tutone";
        final Boolean IS_HR = Boolean.FALSE;

        Employee employee = new Employee();

        employee.setId(ID);
        employee.setEmployeeId(EMPLOYEE_ID);
        employee.setAgencyCode(AGENCY_CODE);
        employee.setAgencyName(AGENCY_NAME);
        employee.setDateOfBirth(DOB);
        employee.setDivisionCode(DIVISION_CODE);
        employee.setDivisionName(DIVISION_NAME);
        employee.setFirstName(FIRST_NAME);
        employee.setLastName(LAST_NAME);
        employee.setEmail(EMAIL);
        employee.setHR(IS_HR);
        employee.setSupervisor(SUPERVISOR);

        // Not used in the VCS workflow:
        final String ALT_FIRST_NAME = "Putting a value here, because code coverage";
        final String ALT_LAST_NAME = "Putting a value here too, because code coverage";
        final String ALT_EMAIL = "Putting a value here as well, because code coverage";

        employee.setAltFirstName(ALT_FIRST_NAME);
        employee.setAltLastName(ALT_LAST_NAME);
        employee.setAlternateEmail(ALT_EMAIL);

        Employee other = new Employee();

        other.setId(ID);
        other.setEmployeeId(EMPLOYEE_ID);
        other.setAgencyCode(AGENCY_CODE);
        other.setAgencyName(AGENCY_NAME);
        other.setDateOfBirth(DOB);
        other.setDivisionCode(DIVISION_CODE);
        other.setDivisionName(DIVISION_NAME);
        other.setFirstName(FIRST_NAME);
        other.setLastName(LAST_NAME);
        other.setEmail(EMAIL);
        other.setHR(IS_HR);
        other.setSupervisor(SUPERVISOR);

        other.setAltFirstName(ALT_FIRST_NAME);
        other.setAltLastName(ALT_LAST_NAME);
        other.setAlternateEmail(ALT_EMAIL);

        // Set up mocks

        // Run test & Validate - Multiple cases below:

        // Run test & Validate: AgencyCode
        other.setAgencyCode(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setAgencyCode(AGENCY_CODE);

        // Run test & Validate: AgencyName
        other.setAgencyName(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setAgencyName(AGENCY_NAME);

        // Run test & Validate: AltFirstName
        other.setAltFirstName(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setAltFirstName(ALT_FIRST_NAME);

        // Run test & Validate: AltLastName
        other.setAltLastName(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setAltLastName(ALT_LAST_NAME);

        // Run test & Validate: AlternateEmail
        other.setAlternateEmail(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setAlternateEmail(ALT_EMAIL);

        // Run test & Validate: DateOfBirth
        other.setDateOfBirth(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setDateOfBirth(DOB);

        // Run test & Validate: DivisionCode
        other.setDivisionCode(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setDivisionCode(DIVISION_CODE);

        // Run test & Validate: DivisionName
        other.setDivisionName(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setDivisionName(DIVISION_NAME);

        // Run test & Validate: Email
        other.setEmail(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setEmail(EMAIL);

        // Run test & Validate: EmployeeId
        other.setEmployeeId(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setEmployeeId(EMPLOYEE_ID);

        // Run test & Validate: FirstName
        other.setFirstName(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setFirstName(FIRST_NAME);

        // Run test & Validate: HR
        other.setHR(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setHR(IS_HR);

        // Run test & Validate: LastName
        other.setLastName(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setLastName(LAST_NAME);

        // Run test & Validate: LastName
        other.setSupervisor(null);

        assertNotEquals(
                other,
                employee,
                "Employees should not be equal unless all fields are equal between then");

        assertNotEquals(
                employee.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setSupervisor(SUPERVISOR);

    }

    // Previously existing tests to refactor:

    @Test
    public void employeeLogCreationTest() {
        Employee e1 = createEmployee(false);
        EmployeeLog employeeLog = e1.asEmployeeLog();
        Employee e2 = asEmployee(employeeLog);

        assertFalse(new EmployeeDiff(e1, e2).ldapFieldChanged());
    }

    private Employee asEmployee(EmployeeLog employeeLog) {
        Employee tmp = new Employee();
        tmp.setAgencyCode(employeeLog.getAgencyCode());
        tmp.setAgencyName(employeeLog.getAgencyName());
        tmp.setDivisionCode(employeeLog.getDivisionCode());
        tmp.setDivisionName(employeeLog.getDivisionName());
        tmp.setEmail(employeeLog.getEmail());
        tmp.setFirstName(employeeLog.getFirstName());
        // tmp.setFullTimePartTime(employeeLog.getFullTimePartTime());
        tmp.setHR(employeeLog.isHR());
        tmp.setLastName(employeeLog.getLastName());
        // tmp.setMiddleName(employeeLog.getMiddleName());
        // tmp.setNcid(employeeLog.getNcid());
        tmp.setSupervisor(employeeLog.getSupervisor());
        // tmp.setUsername(employeeLog.getUsername());
        // tmp.setUserType(employeeLog.getUserType());
        tmp.setEmployeeId(employeeLog.getEmployeeId());
        return tmp;
    }
}
