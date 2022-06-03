package com.redhat.vcs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;

/**
 * This class contains the unit (logic / flow) tests for Vaccination.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 *
 * Note: Due to the (unenforced) contract between equals and hashCode, both methods will be tested together.
 */
public class VaccinationTest {
    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the Vaccination
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final String VACCINE_TYPE = "Should this be an enum?";
        final String INNOCULATION_DATE = "Should this be a date?";
        final String LOT_NUMBER = "Lots and lots";

        Vaccination vaccination = new Vaccination();

        vaccination.setVaccineType(VACCINE_TYPE);
        vaccination.setInoculationDate(INNOCULATION_DATE);
        vaccination.setLotNumber(LOT_NUMBER);

        final String EXPECTED_VACCINATION_STRING = "Vaccination{" +
                "vaccineType='" + VACCINE_TYPE + '\'' +
                ", inoculationDate='" + INNOCULATION_DATE + '\'' +
                ", lotNumber='" + LOT_NUMBER + '\'' +
                '}';

        // Set up mocks

        // Run test
        String vaccinationString = vaccination.toString();

        // Validate
        assertEquals(
                EXPECTED_VACCINATION_STRING,
                vaccinationString,
                "Vaccination String does not match the expected format");
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
        final String VACCINE_TYPE = "Should this be an enum?";
        final String INNOCULATION_DATE = "Should this be a date?";
        final String LOT_NUMBER = "Lots and lots";

        Vaccination vaccination = new Vaccination();

        vaccination.setVaccineType(VACCINE_TYPE);
        vaccination.setInoculationDate(INNOCULATION_DATE);
        vaccination.setLotNumber(LOT_NUMBER);

        Vaccination other = new Vaccination();

        other.setVaccineType(VACCINE_TYPE);
        other.setInoculationDate(INNOCULATION_DATE);
        other.setLotNumber(LOT_NUMBER);

        // Set up mocks

        // Run test
        boolean equals = vaccination.equals(other);

        // Validate
        assertTrue(
                equals,
                "Vaccination constructed from the same values should be equal");

        assertEquals(
                vaccination.hashCode(),
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
        Vaccination vaccination = new Vaccination();
        Vaccination other = vaccination;

        // Set up mocks

        // Run test
        boolean equals = vaccination.equals(other);

        // Validate
        assertTrue(
                equals,
                "An object should always be equal to itself");

        assertEquals(
                vaccination.hashCode(),
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
        Vaccination vaccination = new Vaccination();
        Vaccination other = null;

        // Set up mocks

        // Run test
        boolean equals = vaccination.equals(other);

        // Validate
        assertFalse(
                equals,
                "The equals method should never return true for a null value");

        assertNotEquals(
                vaccination.hashCode(),
                // NOTE: Using Objects.hash, since `other` (null) cannot be dereferenced
                Objects.hash(other),
                "When two objects are not equal, their hash codes should also be not equal");
    }

    /**
     * This "equals(Object)" test is a happy path validation, for the following condition(s):
     *     - Other object is not a Vaccination
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
        Vaccination vaccination = new Vaccination();
        Long other = 42L;

        // Set up mocks

        // Run test
        boolean equals = vaccination.equals(other);

        // Validate
        assertFalse(
                equals,
                "Objects of different classes should never be equal");

        assertNotEquals(
                vaccination.hashCode(),
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
        final String VACCINE_TYPE = "Should this be an enum?";
        final String INNOCULATION_DATE = "Should this be a date?";
        final String LOT_NUMBER = "Lots and lots";

        Vaccination vaccination = new Vaccination();

        vaccination.setVaccineType(VACCINE_TYPE);
        vaccination.setInoculationDate(INNOCULATION_DATE);
        vaccination.setLotNumber(LOT_NUMBER);

        Vaccination other = new Vaccination();

        other.setVaccineType(VACCINE_TYPE);
        other.setInoculationDate(INNOCULATION_DATE);
        other.setLotNumber(LOT_NUMBER);

        // Set up mocks

        // Run test & Validate - Multiple cases below:

        // Run test & Validate: VaccineType
        other.setVaccineType(null);

        assertNotEquals(
                other,
                vaccination,
                "Vaccination should not be equal unless all fields are equal between then");

        assertNotEquals(
                vaccination.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setVaccineType(VACCINE_TYPE);

        // Run test & Validate: InoculationDate
        other.setInoculationDate(null);

        assertNotEquals(
                other,
                vaccination,
                "Vaccination should not be equal unless all fields are equal between then");

        assertNotEquals(
                vaccination.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setInoculationDate(INNOCULATION_DATE);

        // Run test & Validate: LotNumber
        other.setLotNumber(null);

        assertNotEquals(
                other,
                vaccination,
                "Vaccination should not be equal unless all fields are equal between then");

        assertNotEquals(
                vaccination.hashCode(),
                other.hashCode(),
                "When two objects are not equal, their hash codes should also be not equal");

        other.setLotNumber(LOT_NUMBER);
    }
}
