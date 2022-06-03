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
 * This class contains the unit (logic / flow) tests for Proof.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
public class ProofTest {
    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the Proof
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final String TYPE = "The best!";
        final String IMAGE = "icanhascheezburger.jpg";
        // Proof will be tested separately
        final List<Vaccination> VACCINATIONS = null;

        Proof proof = new Proof();

        proof.setType(TYPE);
        proof.setImage(IMAGE);
        proof.setVaccinations(VACCINATIONS);

        final String EXPECTED_PROOF_STRING = "Proof{"
                + "type='" + TYPE + '\''
                + ", vaccinations=" + VACCINATIONS + '\''
                + ", image=" + IMAGE
                + '}';

        // Set up mocks

        // Run test
        String proofString = proof.toString();

        // Validate
        assertEquals(
                EXPECTED_PROOF_STRING,
                proofString,
                "Proof String does not match the expected format");
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
        final String TYPE = "The best!";
        final String IMAGE = "icanhascheezburger.jpg";
        final List<Vaccination> VACCINATIONS = null;

        Proof proof = new Proof();

        proof.setType(TYPE);
        proof.setImage(IMAGE);
        proof.setVaccinations(VACCINATIONS);

        Proof other = new Proof();

        other.setType(TYPE);
        other.setImage(IMAGE);
        other.setVaccinations(VACCINATIONS);

        // Set up mocks

        // Run test
        boolean equals = proof.equals(other);

        // Validate
        assertTrue(
                equals,
                "Proof constructed from the same values should be equal");

        assertEquals(
                proof.hashCode(),
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
        Proof proof = new Proof();
        Proof other = proof;

        // Set up mocks

        // Run test
        boolean equals = proof.equals(other);

        // Validate
        assertTrue(
                equals,
                "An object should always be equal to itself");

        assertEquals(
                proof.hashCode(),
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
        Proof proof = new Proof();
        Proof other = null;

        // Set up mocks

        // Run test
        boolean equals = proof.equals(other);

        // Validate
        assertFalse(
                equals,
                "The equals method should never return true for a null value");

        assertNotEquals(
                proof.hashCode(),
                // NOTE: Using Objects.hash, since `other` (null) cannot be dereferenced
                Objects.hash(other),
                "When two objects are not equal, their hash codes should also be not equal");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Other object is not a Proof
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
        Proof proof = new Proof();
        Long other = 42L;

        // Set up mocks

        // Run test
        boolean equals = proof.equals(other);

        // Validate
        assertFalse(
                equals,
                "Objects of different classes should never be equal");

        assertNotEquals(
                proof.hashCode(),
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
        final String TYPE = "The best!";
        final String IMAGE = "icanhascheezburger.jpg";
        final List<Vaccination> VACCINATIONS = new ArrayList<>();

        Proof proof = new Proof();

        proof.setType(TYPE);
        proof.setImage(IMAGE);
        proof.setVaccinations(VACCINATIONS);

        Proof other = new Proof();

        other.setType(TYPE);
        other.setImage(IMAGE);
        other.setVaccinations(VACCINATIONS);

        // Set up mocks

        // Run test & Validate - Multiple cases below:

        // Run test & Validate: Type
        other.setType(null);

        assertNotEquals(
                other,
                proof,
                "Proof should not be equal unless all fields are equal between then");

        assertNotEquals(
                proof.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setType(TYPE);

        // Run test & Validate: Image
        other.setImage(null);

        assertNotEquals(
                other,
                proof,
                "Proof should not be equal unless all fields are equal between then");

        assertNotEquals(
                proof.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setImage(IMAGE);

        // Run test & Validate: LotNumber
        other.setVaccinations(null);

        assertNotEquals(
                other,
                proof,
                "Proof should not be equal unless all fields are equal between then");

        assertNotEquals(
                proof.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setVaccinations(VACCINATIONS);
    }
}
