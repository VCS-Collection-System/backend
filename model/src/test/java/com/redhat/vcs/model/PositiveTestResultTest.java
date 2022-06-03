package com.redhat.vcs.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * This class contains the unit (logic / flow) tests for PositiveTestResult.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 *
 * Note: Due to the (unenforced) contract between equals and hashCode, both methods will be tested together.
 */
public class PositiveTestResultTest {
    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the PositiveTestResult
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final Long ID = 3000L;
        final String EMPLOYEE_ID = Long.toString(ID);
        final String AGENCY_CODE = "FLAG";
        final String AGENCY_NAME = "Foundation for Law and Government";
        final String DIVISION_CODE = "KITT";
        final String DIVISION_NAME = "Knight Industries Three Thousand";
        final String SUBMITTED_BY = "Michael Knight";
        final String SUBMISSION_TYPE = "positive test";
        final LocalDate COVID_TEST_DATE = LocalDate.of(2020, 2, 2);
        final LocalDateTime RECORDED_DATE = LocalDateTime.now();
        // Attachment will be verified in its own test class
        final Attachment ATTACHMENT = null;

        PositiveTestResult result = new PositiveTestResult();

        result.setAgencyCode(AGENCY_CODE);
        result.setAgencyName(AGENCY_NAME);
        result.setCovidTestDate(COVID_TEST_DATE);
        result.setId(ID);
        result.setEmployeeId(EMPLOYEE_ID);
        result.setDivisionCode(DIVISION_CODE);
        result.setDivisionName(DIVISION_NAME);
        result.setSubmittedBy(SUBMITTED_BY);
        result.setRecordedDate(RECORDED_DATE);
        result.setAttachment(ATTACHMENT);
        result.setSubmissionType(SUBMISSION_TYPE);

        final String EXPECTED_RESULT_REGEX = "PositiveTestResult \\["
                + "agencyCode=" + AGENCY_CODE
                + ", agencyName=" + AGENCY_NAME
                + ", attachment=" + ATTACHMENT
                + ", covidTestDate=" + COVID_TEST_DATE
                + ", divisionCode=" + DIVISION_CODE
                + ", divisionName=" + DIVISION_NAME
                + ", employeeId=" + "\\*+"
                + ", id=" + ID
                + ", recordedDate=" + RECORDED_DATE
                + ", submittedBy=" + SUBMITTED_BY
                + ", submissionType=" + SUBMISSION_TYPE
                + "\\]";

        // Set up mocks

        // Run test
        String resultString = result.toString();

        // Validate
        assertTrue(
                resultString.matches(EXPECTED_RESULT_REGEX),
                "PositiveTestResult String does not match the expected format");
    }
}
