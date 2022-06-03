package com.redhat.vcs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;

/**
 * This class contains the unit (logic / flow) tests for VaxProofAutomaticApprovalResponse.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 *
 * Note: Due to the (unenforced) contract between equals and hashCode, both methods will be tested together.
 */
public class VaxProofAutomaticApprovalResponseTest {
    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the VaxProofAutomaticApprovalResponse
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final String CORRELATION_ID = "12121";
        final Integer CONFIDENCE_SCORE = 10;
        final Boolean HARD_FAIL = Boolean.FALSE;
        final String REPORT = "Direct";
        final ProofType PROOF_TYPE = ProofType.SMARTHEALTH;

        VaxProofAutomaticApprovalResponse response = new VaxProofAutomaticApprovalResponse();

        response.setCorrelationId(CORRELATION_ID);
        response.setConfidenceScore(CONFIDENCE_SCORE);
        response.setHardFail(HARD_FAIL);
        response.setReport(REPORT);
        response.setProofType(PROOF_TYPE);

        final String EXPECTED_RESPONSE_STRING = "VaxProofAutomaticApprovalResponse ["
                + "correlationId=" + CORRELATION_ID
                + ", confidenceScore=" + CONFIDENCE_SCORE
                + ", hardFail=" + HARD_FAIL
                + ", report=" + REPORT
                + ", proofType=" + PROOF_TYPE
                + "]";

        // Set up mocks

        // Run test
        String responseString = response.toString();

        // Validate
        assertEquals(
                EXPECTED_RESPONSE_STRING,
                responseString,
                "VaxProofAutomaticApprovalResponse String does not match the expected format");
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
        final String CORRELATION_ID = "12121";
        final Integer CONFIDENCE_SCORE = 10;
        final Boolean HARD_FAIL = Boolean.FALSE;
        final String REPORT = "Direct";
        final ProofType PROOF_TYPE = ProofType.SMARTHEALTH;

        VaxProofAutomaticApprovalResponse response = new VaxProofAutomaticApprovalResponse();

        response.setCorrelationId(CORRELATION_ID);
        response.setConfidenceScore(CONFIDENCE_SCORE);
        response.setHardFail(HARD_FAIL);
        response.setReport(REPORT);
        response.setProofType(PROOF_TYPE);

        VaxProofAutomaticApprovalResponse other = new VaxProofAutomaticApprovalResponse();

        other.setCorrelationId(CORRELATION_ID);
        other.setConfidenceScore(CONFIDENCE_SCORE);
        other.setHardFail(HARD_FAIL);
        other.setReport(REPORT);
        other.setProofType(PROOF_TYPE);

        // Set up mocks

        // Run test
        boolean equals = response.equals(other);

        // Validate
        assertTrue(
                equals,
                "VaxProofAutomaticApprovalResponses constructed from the same values should be equal");

        assertEquals(
                response.hashCode(),
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
        VaxProofAutomaticApprovalResponse response = new VaxProofAutomaticApprovalResponse();
        VaxProofAutomaticApprovalResponse other = response;

        // Set up mocks

        // Run test
        boolean equals = response.equals(other);

        // Validate
        assertTrue(
                equals,
                "An object should always be equal to itself");

        assertEquals(
                response.hashCode(),
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
        VaxProofAutomaticApprovalResponse response = new VaxProofAutomaticApprovalResponse();
        VaxProofAutomaticApprovalResponse other = null;

        // Set up mocks

        // Run test
        boolean equals = response.equals(other);

        // Validate
        assertFalse(
                equals,
                "The equals method should never return true for a null value");

        assertNotEquals(
                response.hashCode(),
                // NOTE: Using Objects.hash, since `other` (null) cannot be dereferenced
                Objects.hash(other),
                "When two objects are not equal, their hash codes should also be not equal");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Other object is not a VaxProofAutomaticApprovalResponse
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
        VaxProofAutomaticApprovalResponse response = new VaxProofAutomaticApprovalResponse();
        Long other = 42L;

        // Set up mocks

        // Run test
        boolean equals = response.equals(other);

        // Validate
        assertFalse(
                equals,
                "Objects of different classes should never be equal");

        assertNotEquals(
                response.hashCode(),
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
        final String CORRELATION_ID = "12121";
        final Integer CONFIDENCE_SCORE = 10;
        final Boolean HARD_FAIL = Boolean.FALSE;
        final String REPORT = "Direct";
        final ProofType PROOF_TYPE = ProofType.SMARTHEALTH;

        VaxProofAutomaticApprovalResponse response = new VaxProofAutomaticApprovalResponse();

        response.setCorrelationId(CORRELATION_ID);
        response.setConfidenceScore(CONFIDENCE_SCORE);
        response.setHardFail(HARD_FAIL);
        response.setReport(REPORT);
        response.setProofType(PROOF_TYPE);

        VaxProofAutomaticApprovalResponse other = new VaxProofAutomaticApprovalResponse();

        other.setCorrelationId(CORRELATION_ID);
        other.setConfidenceScore(CONFIDENCE_SCORE);
        other.setHardFail(HARD_FAIL);
        other.setReport(REPORT);
        other.setProofType(PROOF_TYPE);

        // Set up mocks

        // Run test & Validate - Multiple cases below:

        // Run test & Validate: CorrelationId
        other.setCorrelationId(null);

        assertNotEquals(
                other,
                response,
                "VaxProofAutomaticApprovalResponse should not be equal unless all fields are equal between then");

        assertNotEquals(
                response.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setCorrelationId(CORRELATION_ID);

        // Run test & Validate: ConfidenceScore
        other.setConfidenceScore(null);

        assertNotEquals(
                other,
                response,
                "VaxProofAutomaticApprovalResponse should not be equal unless all fields are equal between then");

        assertNotEquals(
                response.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setConfidenceScore(CONFIDENCE_SCORE);

        // Run test & Validate: HardFail
        other.setHardFail(null);

        assertNotEquals(
                other,
                response,
                "VaxProofAutomaticApprovalResponse should not be equal unless all fields are equal between then");

        assertNotEquals(
                response.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setHardFail(HARD_FAIL);

        // Run test & Validate: Report
        other.setReport(null);

        assertNotEquals(
                other,
                response,
                "VaxProofAutomaticApprovalResponse should not be equal unless all fields are equal between then");

        assertNotEquals(
                response.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setReport(REPORT);

        // Run test & Validate: ProofType
        other.setProofType(null);

        assertNotEquals(
                other,
                response,
                "VaxProofAutomaticApprovalResponse should not be equal unless all fields are equal between then");

        assertNotEquals(
                response.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setProofType(PROOF_TYPE);
    }
}
