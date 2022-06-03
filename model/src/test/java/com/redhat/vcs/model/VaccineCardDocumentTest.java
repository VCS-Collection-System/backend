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
 * This class contains the unit (logic / flow) tests for VaccineCardDocument.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
@ExtendWith(MockitoExtension.class)
public class VaccineCardDocumentTest {
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
        final VaccineBrand BRAND = VaccineBrand.ASTRAZENECA;
        final LocalDate ADMIN_DATE = LocalDate.of(2000, 1, 1);
        final Integer SHOT_NUMBER = 5;

        VaccineCardDocument document = new VaccineCardDocument();

        document.setVaccineBrand(BRAND);
        document.setVaccineAdministrationDate(ADMIN_DATE);
        document.setVaccineShotNumber(SHOT_NUMBER);

        // Set up mocks

        // Run test
        List<String> errors = document.validate();

        // Validate
        assertTrue(errors.isEmpty(), "There should not be any validation errors when all fields are provided");
    }

    /**
     * This "validate()" test is a negative path validation, for the following condition(s):
     *     - Vaccine Brand not provided
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a List<String> which:
     *     - Contains exactly one item:
     *         - A String indicating that the brand is missing
     */
    @Test
    public void test_validate_fail_noBrand() {
        // Set up data
        final VaccineBrand BRAND = null;
        final LocalDate ADMIN_DATE = LocalDate.of(2000, 1, 1);
        final Integer SHOT_NUMBER = 2;

        VaccineCardDocument document = new VaccineCardDocument();

        document.setVaccineBrand(BRAND);
        document.setVaccineAdministrationDate(ADMIN_DATE);
        document.setVaccineShotNumber(SHOT_NUMBER);

        // Set up mocks

        // Run test
        List<String> errors = document.validate();

        // Validate
        assertEquals(
                1,
                errors.size(),
                "When test result is provided and test date is not, there should be 1 error");

        assertTrue(
                errors.contains("Vaccine brand is missing."),
                "Error should indicate that admin date is missing");
    }

    /**
     * This "validate()" test is a negative path validation, for the following condition(s):
     *     - Vaccine Admin Date not provided
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a List<String> which:
     *     - Contains exactly one item:
     *         - A String indicating that the admin date is missing
     */
    @Test
    public void test_validate_fail_noAdminDate() {
        // Set up data
        final VaccineBrand BRAND = VaccineBrand.ASTRAZENECA;
        final LocalDate ADMIN_DATE = null;
        final Integer SHOT_NUMBER = 2;

        VaccineCardDocument document = new VaccineCardDocument();

        document.setVaccineBrand(BRAND);
        document.setVaccineAdministrationDate(ADMIN_DATE);
        document.setVaccineShotNumber(SHOT_NUMBER);

        // Set up mocks

        // Run test
        List<String> errors = document.validate();

        // Validate
        assertEquals(
                1,
                errors.size(),
                "When test result is provided and test date is not, there should be 1 error");

        assertTrue(
                errors.contains("Vaccine administration date is missing."),
                "Error should indicate that admin date is missing");
    }

    
   

    /**
     * This "validate()" test is a negative path validation, for the following condition(s):
     *     - Too few shots listed
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a List<String> which:
     *     - Contains exactly one item:
     *         - A String indicating that the number of shots is invalid
     */
    @Test
    public void test_validate_fail_tooFewShots() {
        // Set up data
        final VaccineBrand BRAND = VaccineBrand.ASTRAZENECA;
        final LocalDate ADMIN_DATE = LocalDate.of(2000, 1, 1);
        final Integer SHOT_NUMBER = 0;

        VaccineCardDocument document = new VaccineCardDocument();

        document.setVaccineBrand(BRAND);
        document.setVaccineAdministrationDate(ADMIN_DATE);
        document.setVaccineShotNumber(SHOT_NUMBER);

        // Set up mocks

        // Run test
        List<String> errors = document.validate();

        // Validate
        assertEquals(
                1,
                errors.size(),
                "When test date is provided and test result is not, there should be 1 error");

        assertTrue(
                errors.contains("Invalid vaccine shot number '" + SHOT_NUMBER +"'"),
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
        final LocalDate ADMIN_DATE = LocalDate.of(1985, 1, 1);
        final VaccineBrand BRAND = VaccineBrand.NOVAVAX;
        final Integer SHOT_NUMBER = 1;

        final LocalDateTime SUBMISSION_DATE = LocalDateTime.of(1815, 12, 10, 0, 0);
        final String SUBMITTED_BY = "Lord & Lady Byron";
        final String LAST_UPDATED_BY = "N/A";

        final Long ID = 10L;
        final Boolean AUTO_APPROVED = Boolean.FALSE;

        // toString on these fields will be tested in their respective unit test classes
        final Employee EMPLOYEE = null;
        final Attachment ATTACHMENT = null;
        final DocumentReview REVIEW = null;

        final String EXPECTED_DOCUMENT_STRING = "VaccineCardDocument ["
                + "vaccineAdministrationDate=" + ADMIN_DATE
                + ", vaccineBrand=" + BRAND
                + ", vaccineShotNumber=" + SHOT_NUMBER
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

        VaccineCardDocument document = new VaccineCardDocument();

        // Fields directly from VaccineCardDocument
        document.setVaccineAdministrationDate(ADMIN_DATE);
        document.setVaccineBrand(BRAND);
        document.setVaccineShotNumber(SHOT_NUMBER);

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

    /**
     * TODO: This test is added for the sole purpose of adding code coverage; these methods appear to be DEAD CODE, so
     * there is no way to test them via normal code execution.
     */
    @Test
    public void codeCoverage_gettersAndSetters() {
        // Set up data
        final Integer SCORE = 99;
        final Boolean FULL_VAX = Boolean.TRUE;
        final ProofType PROOF_TYPE = ProofType.OTHER;
        final String LOT_NUMBER = "Pretty sure this is dead code anyway.....";
        final String CLINIC_NAME = "Unused field";

        VaccineCardDocument document = new VaccineCardDocument();
        document.setConfidenceScore(SCORE);
        document.setFullVaccinatedFlag(FULL_VAX);
        document.setProofType(PROOF_TYPE);
        document.setLotNumber(LOT_NUMBER);
        document.setClinicName(CLINIC_NAME);

        // Set up mocks

        // Run test & Validate
        assertEquals(
                SCORE,
                document.getConfidenceScore(),
                "Getter did not return value from setter: ConfidenceScore");

        assertEquals(
                FULL_VAX,
                document.isFullVaccinatedFlag(),
                "Getter did not return value from setter: FullVaccinatedFlag");

        assertEquals(
                PROOF_TYPE,
                document.getProofType(),
                "Getter did not return value from setter: ProofType");

        assertEquals(
                LOT_NUMBER,
                document.getLotNumber(),
                "Getter did not return value from setter: LotNumber");

        assertEquals(
                CLINIC_NAME,
                document.getClinicName(),
                "Getter did not return value from setter: ClinicName");
    }
}
