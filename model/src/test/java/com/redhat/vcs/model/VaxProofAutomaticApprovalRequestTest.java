package com.redhat.vcs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

/**
 * This class contains the unit (logic / flow) tests for VaxProofAutomaticApprovalRequest.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 *
 * Note: Due to the (unenforced) contract between equals and hashCode, both methods will be tested together.
 */
public class VaxProofAutomaticApprovalRequestTest {
    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the VaxProofAutomaticApprovalRequest
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final String CORRELATION_ID = "12121";
        final String FIRST_NAME = "Elvis";
        final String LAST_NAME = "Stojko";
        final String DOB = "Dobby is free! (should this be a date field?)";
        // Proof will be verified in its own test class
        final List<Proof> PROOFS = null;

        VaxProofAutomaticApprovalRequest request = new VaxProofAutomaticApprovalRequest();

        request.setCorrelationId(CORRELATION_ID);
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setDob(DOB);
        request.setProofs(PROOFS);

        final String EXPECTED_REQUEST_REGEX = "VaxProofAutomaticApprovalRequest\\{"
                + "correlationId='" + CORRELATION_ID + '\''
                + ", firstName='" + "\\*+" + '\''
                + ", lastName='" + "\\*+" + '\''
                + ", dob='" + "\\*\\*/\\*\\*/\\*\\*\\*\\*" + '\''
                + ", proofs=" + PROOFS
                + "\\}";

        // Set up mocks

        // Run test
        String requestString = request.toString();

        // Validate
        assertTrue(
                requestString.matches(EXPECTED_REQUEST_REGEX),
                "VaxProofAutomaticApprovalRequest String does not match the expected format");
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
        final String FIRST_NAME = "Elvis";
        final String LAST_NAME = "Stojko";
        final String DOB = "Dobby is free! (should this be a date field?)";
        // Proof will be verified in its own test class
        final List<Proof> PROOFS = null;

        VaxProofAutomaticApprovalRequest request = new VaxProofAutomaticApprovalRequest();

        request.setCorrelationId(CORRELATION_ID);
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setDob(DOB);
        request.setProofs(PROOFS);

        VaxProofAutomaticApprovalRequest other = new VaxProofAutomaticApprovalRequest();

        other.setCorrelationId(CORRELATION_ID);
        other.setFirstName(FIRST_NAME);
        other.setLastName(LAST_NAME);
        other.setDob(DOB);
        other.setProofs(PROOFS);

        // Set up mocks

        // Run test
        boolean equals = request.equals(other);

        // Validate
        assertTrue(
                equals,
                "VaxProofAutomaticApprovalRequests constructed from the same values should be equal");

        assertEquals(
                request.hashCode(),
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
        VaxProofAutomaticApprovalRequest request = new VaxProofAutomaticApprovalRequest();
        VaxProofAutomaticApprovalRequest other = request;

        // Set up mocks

        // Run test
        boolean equals = request.equals(other);

        // Validate
        assertTrue(
                equals,
                "An object should always be equal to itself");

        assertEquals(
                request.hashCode(),
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
        VaxProofAutomaticApprovalRequest request = new VaxProofAutomaticApprovalRequest();
        VaxProofAutomaticApprovalRequest other = null;

        // Set up mocks

        // Run test
        boolean equals = request.equals(other);

        // Validate
        assertFalse(
                equals,
                "The equals method should never return true for a null value");

        assertNotEquals(
                request.hashCode(),
                // NOTE: Using Objects.hash, since `other` (null) cannot be dereferenced
                Objects.hash(other),
                "When two objects are not equal, their hash codes should also be not equal");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Other object is not a VaxProofAutomaticApprovalRequest
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
        VaxProofAutomaticApprovalRequest request = new VaxProofAutomaticApprovalRequest();
        Long other = 42L;

        // Set up mocks

        // Run test
        boolean equals = request.equals(other);

        // Validate
        assertFalse(
                equals,
                "Objects of different classes should never be equal");

        assertNotEquals(
                request.hashCode(),
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
        // Set up data
        final String CORRELATION_ID = "12121";
        final String FIRST_NAME = "Elvis";
        final String LAST_NAME = "Stojko";
        final String DOB = "Dobby is free! (should this be a date field?)";
        final List<Proof> PROOFS = new ArrayList<>();

        VaxProofAutomaticApprovalRequest request = new VaxProofAutomaticApprovalRequest();

        request.setCorrelationId(CORRELATION_ID);
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setDob(DOB);
        request.setProofs(PROOFS);

        VaxProofAutomaticApprovalRequest other = new VaxProofAutomaticApprovalRequest();

        other.setCorrelationId(CORRELATION_ID);
        other.setFirstName(FIRST_NAME);
        other.setLastName(LAST_NAME);
        other.setDob(DOB);
        other.setProofs(PROOFS);

        // Set up mocks

        // Run test & Validate - Multiple cases below:

        // Run test & Validate: CorrelationId
        other.setCorrelationId(null);

        assertNotEquals(
                other,
                request,
                "VaxProofAutomaticApprovalRequest should not be equal unless all fields are equal between then");

        assertNotEquals(
                request.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setCorrelationId(CORRELATION_ID);

        // Run test & Validate: FirstName
        other.setFirstName(null);

        assertNotEquals(
                other,
                request,
                "VaxProofAutomaticApprovalRequest should not be equal unless all fields are equal between then");

        assertNotEquals(
                request.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setFirstName(FIRST_NAME);

        // Run test & Validate: LastName
        other.setLastName(null);

        assertNotEquals(
                other,
                request,
                "VaxProofAutomaticApprovalRequest should not be equal unless all fields are equal between then");

        assertNotEquals(
                request.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setLastName(LAST_NAME);

        // Run test & Validate: DOB
        other.setDob(null);

        assertNotEquals(
                other,
                request,
                "VaxProofAutomaticApprovalRequest should not be equal unless all fields are equal between then");

        assertNotEquals(
                request.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setDob(DOB);

        // Run test & Validate: Proofs
        other.setProofs(null);

        assertNotEquals(
                other,
                request,
                "VaxProofAutomaticApprovalRequest should not be equal unless all fields are equal between then");

        assertNotEquals(
                request.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setProofs(PROOFS);
    }
}
