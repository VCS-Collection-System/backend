package com.redhat.vcs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * This class contains the unit (logic / flow) tests for CovidTestResultDocument.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
@ExtendWith(MockitoExtension.class)
public class CovidTestResultDocumentTest {
    /**
     * This "validate()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a List<String> which:
     *     - Should be empty
     */
    @Test
    public void test_validate_success() {
        // Set up data
        CovidTestResultDocument document = new CovidTestResultDocument();

        document.setCovidTestResult(CovidTestResult.POSITIVE);
        document.setCovidTestDate(LocalDate.now());

        // Set up mocks

        // Run test
        List<String> errors = document.validate();

        // Validate
        assertTrue(errors.isEmpty(), "There should not be any validation errors when all fields are provided");
    }

    /**
     * This "validate()" test is a negative path validation, for the following condition(s):
     *     - Covid Test Date not provided
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a List<String> which:
     *     - Contains exactly one item:
     *         - A String indicating that the test date is missing
     */
    @Test
    public void test_validate_fail_noDate() {
        // Set up data
        CovidTestResultDocument document = new CovidTestResultDocument();

        document.setCovidTestResult(CovidTestResult.POSITIVE);
        document.setCovidTestDate(null);

        // Set up mocks

        // Run test
        List<String> errors = document.validate();

        // Validate
        assertEquals(
                1,
                errors.size(),
                "When test result is provided and test date is not, there should be 1 error");

        assertTrue(
                errors.contains("COVID test result date is missing."),
                "Error should indicate that test date is missing");
    }

    /**
     * This "validate()" test is a negative path validation, for the following condition(s):
     *     - Covid Test Result not provided
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a List<String> which:
     *     - Contains exactly one item:
     *         - A String indicating that the test result is missing
     */
    @Test
    public void test_validate_fail_noResult() {
        // Set up data
        CovidTestResultDocument document = new CovidTestResultDocument();

        document.setCovidTestResult(null);
        document.setCovidTestDate(LocalDate.now());

        // Set up mocks

        // Run test
        List<String> errors = document.validate();

        // Validate
        assertEquals(
                1,
                errors.size(),
                "When test date is provided and test result is not, there should be 1 error");

        assertTrue(
                errors.contains("COVID test result outcome is missing."),
                "Error should indicate that test result is missing");
    }

    /**
     * This "validate()" test is a negative path validation, for the following condition(s):
     *     - Covid Test Date not provided
     *     - Covid Test Result not provided
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a List<String> which:
     *     - Contains exactly two items:
     *         - A String indicating that the test date is missing
     *         - A String indicating that the test result is missing
     */
    @Test
    public void test_validate_fail_noDateOrResult() {
        // Set up data
        CovidTestResultDocument document = new CovidTestResultDocument();

        document.setCovidTestResult(null);
        document.setCovidTestDate(null);

        // Set up mocks

        // Run test
        List<String> errors = document.validate();

        // Validate
        assertEquals(
                2,
                errors.size(),
                "When test date and test result are both not provided, there should be 2 errors");

        assertTrue(
                errors.contains("COVID test result outcome is missing."),
                "Error should indicate that test result is missing");
    }

    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the CoverTestResultDocument
     *     - NOTE: Employee, Attachment, and DocumentReview will be null values, as these are tested separately
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final LocalDateTime LAST_UPDATED_TIME = LocalDateTime.of(2021, 11, 3,  11, 40);
        final LocalDate TEST_DATE = LocalDate.of(1985, 1, 1);
        final CovidTestResult TEST_RESULT = CovidTestResult.INCONCLUSIVE;
        final LocalDateTime SUBMISSION_DATE = LocalDateTime.of(1815, 12, 10, 0, 0);
        final String SUBMITTED_BY = "Lord & Lady Byron";
        final String LAST_UPDATED_BY = "N/A";

        final Long ID = 10L;
        final Boolean AUTO_APPROVED = Boolean.FALSE;

        // toString on these fields will be tested in their respective unit test classes
        final Employee EMPLOYEE = null;
        final Attachment ATTACHMENT = null;
        final DocumentReview REVIEW = null;

        final String EXPECTED_DOCUMENT_STRING = "CovidTestResultDocument ["
                + "covidTestDate=" + TEST_DATE
                + ", covidTestResult=" + TEST_RESULT
                + " # "
                + "Document ["
                + "id=" + ID
                + ", employee=" + EMPLOYEE
                + ", attachment=" + ATTACHMENT
                + ", submissionDate=" + SUBMISSION_DATE
                + ", review=" + REVIEW
                + ", submittedBy=" + SUBMITTED_BY
                + ", autoApproved=" + AUTO_APPROVED
                + ", lastUpdatedTime=" + LAST_UPDATED_TIME
                + ", lastUpdatedBy=" + LAST_UPDATED_BY
                + "]"
                + "]";

        CovidTestResultDocument document = new CovidTestResultDocument();

        // Fields directly from CovidTestResultDocument
        document.setCovidTestResult(TEST_RESULT);
        document.setCovidTestDate(TEST_DATE);

        // Fields extended from Document
        document.setId(ID);
        document.setSubmissionDate(SUBMISSION_DATE);
        document.setSubmittedBy(SUBMITTED_BY);
        document.setAutoApproved(AUTO_APPROVED);
        document.setLastUpdatedTime(LAST_UPDATED_TIME);
        document.setLastUpdatedBy(LAST_UPDATED_BY);

        document.setEmployee(EMPLOYEE);
        document.setAttachment(ATTACHMENT);
        document.setReview(REVIEW);

        // Set up mocks

        // Run test
        String documentString = document.toString();

        // Validate
        assertEquals(
                EXPECTED_DOCUMENT_STRING,
                documentString,
                "Document String does not match the expected format");
    }
}
