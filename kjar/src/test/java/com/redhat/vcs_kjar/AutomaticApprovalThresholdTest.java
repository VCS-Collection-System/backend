package com.redhat.vcs_kjar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;

/**
 * This class contains the unit (logic / flow) tests for AutomaticApprovalThreshold.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 *
 * Note: Due to the (unenforced) contract between equals and hashCode, both methods will be tested together.
 */
public class AutomaticApprovalThresholdTest {
    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the AutomaticApprovalThreshold
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final boolean IS_MET = true;

        AutomaticApprovalThreshold threshold = new AutomaticApprovalThreshold();
        threshold.setMet(IS_MET);

        final String EXPECTED_THRESHOLD_STRING = "AutomaticApprovalThreshold ["
                + "isMet=" + IS_MET
                + "]";

        // Set up mocks

        // Run test
        String thresholdString = threshold.toString();

        // Validate
        assertEquals(
                EXPECTED_THRESHOLD_STRING,
                thresholdString,
                "AutomaticApprovalThreshold String does not match the expected format");
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
        final boolean IS_MET = true;

        AutomaticApprovalThreshold threshold = new AutomaticApprovalThreshold();

        threshold.setMet(IS_MET);

        AutomaticApprovalThreshold other = new AutomaticApprovalThreshold();

        other.setMet(IS_MET);

        // Set up mocks

        // Run test
        boolean equals = threshold.equals(other);

        // Validate
        assertTrue(
                equals,
                "AutomaticApprovalThreshold constructed from the same values should be equal");

        assertEquals(
                threshold.hashCode(),
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
        AutomaticApprovalThreshold threshold = new AutomaticApprovalThreshold();
        AutomaticApprovalThreshold other = threshold;

        // Set up mocks

        // Run test
        boolean equals = threshold.equals(other);

        // Validate
        assertTrue(
                equals,
                "An object should always be equal to itself");

        assertEquals(
                threshold.hashCode(),
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
        AutomaticApprovalThreshold threshold = new AutomaticApprovalThreshold();
        AutomaticApprovalThreshold other = null;

        // Set up mocks

        // Run test
        boolean equals = threshold.equals(other);

        // Validate
        assertFalse(
                equals,
                "The equals method should never return true for a null value");

        assertNotEquals(
                threshold.hashCode(),
                // NOTE: Using Objects.hash, since `other` (null) cannot be dereferenced
                Objects.hash(other),
                "When two objects are not equal, their hash codes should also be not equal");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Other object is not a AutomaticApprovalThreshold
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
        AutomaticApprovalThreshold threshold = new AutomaticApprovalThreshold();
        Long other = 42L;

        // Set up mocks

        // Run test
        boolean equals = threshold.equals(other);

        // Validate
        assertFalse(
                equals,
                "Objects of different classes should never be equal");

        assertNotEquals(
                threshold.hashCode(),
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
     */
    @Test
    public void test_equals_hashcode_success_notEqual_differentValues() {
        // Set up data
        final boolean IS_MET = false;

        AutomaticApprovalThreshold threshold = new AutomaticApprovalThreshold();
        AutomaticApprovalThreshold other = new AutomaticApprovalThreshold();

        threshold.setMet(IS_MET);
        other.setMet(!IS_MET);

        // Set up mocks

        // Run test & Validate - Multiple cases below:
        boolean equals = threshold.equals(other);

        // Validate
        assertFalse(
                equals,
                "AutomaticApprovalThreshold should not be equal unless all fields are equal between then");

        assertNotEquals(
                threshold.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");
    }
}
