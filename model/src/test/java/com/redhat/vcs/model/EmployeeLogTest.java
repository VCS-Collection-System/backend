package com.redhat.vcs.model;

import org.junit.jupiter.api.Test;

/**
 * This class contains the unit (logic / flow) tests for EmployeeLog.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 *
 * Note: Due to the (unenforced) contract between equals and hashCode, both methods will be tested together.
 */
public class EmployeeLogTest {
    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the EmployeeLog
     *
     * TODO: This test does not do what it needs to; need the ability to pass a clock into LocalDate.now, in order to
     *       control the value for testing.
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
        final String EMAIL = "jenny@example.org";
        final String FIRST_NAME = "Jenny";
        final String MIDDLE_NAME = "Jenn";
        final String LAST_NAME = "Jenni";
        final String SUPERVISOR = "Tommy Tutone";
        final Boolean IS_HR = Boolean.FALSE;

        final String WORKFORCE_ID = "WID";
        final String FULL_TIME_PART_TIME = "All the time!";
        final String NCID = "NCIS";
        final String USER_TYPE = "Awesome";
        final String USERNAME = "Sauce";

        EmployeeLog employeeLog = new EmployeeLog();

        employeeLog.setId(ID);
        employeeLog.setEmployeeId(EMPLOYEE_ID);
        employeeLog.setAgencyCode(AGENCY_CODE);
        employeeLog.setAgencyName(AGENCY_NAME);
        employeeLog.setDivisionCode(DIVISION_CODE);
        employeeLog.setDivisionName(DIVISION_NAME);
        employeeLog.setFirstName(FIRST_NAME);
        employeeLog.setLastName(LAST_NAME);
        employeeLog.setEmail(EMAIL);
        employeeLog.setHR(IS_HR);
        employeeLog.setSupervisor(SUPERVISOR);

        employeeLog.setWorkforceId(WORKFORCE_ID);
        employeeLog.setFullTimePartTime(FULL_TIME_PART_TIME);
        employeeLog.setMiddleName(MIDDLE_NAME);
        employeeLog.setNcid(NCID);
        employeeLog.setUserType(USER_TYPE);
        employeeLog.setUsername(USERNAME);

        final String EXPECTED_EMPLOYEE_LOG_STRING = "EmployeeLog ["
                + "agencyCode=" + AGENCY_CODE
                + ", agencyName=" + AGENCY_NAME
                + ", divisionCode=" + DIVISION_CODE
                + ", divisionName=" + DIVISION_NAME
                + ", email=" + EMAIL
                + ", firstName=" + FIRST_NAME
                + ", fullTimePartTime=" + FULL_TIME_PART_TIME
                + ", id=" + ID
                + ", isHR=" + IS_HR
                + ", lastName=" + LAST_NAME
                // TODO: No associated setter for timestamp? Like, not even a no-op, if it's read-only?
                + ", timestamp=" + null
                + ", middleName=" + MIDDLE_NAME
                + ", ncid=" + NCID
                + ", supervisor=" + SUPERVISOR
                + ", userType=" + USER_TYPE
                + ", username=" + USERNAME
                + "]";

        // Set up mocks

        // Run test
        String employeeLogString = employeeLog.toString();

        // Validate
        /*assertEquals(
                EXPECTED_EMPLOYEE_LOG_STRING,
                employeeLogString,
                "EmployeeLog String does not match the expected format");*/
    }
}
