package com.redhat.vcs_kjar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.redhat.vcs.model.VaccineBrand;

import org.junit.Test;

public class KjarUtilsTest {

    /**
     * This test verifies that passing a non-blank string to nonBlank returns true
     */
    @Test
    public void test_nonBlank_true() {
        assertTrue("A non-empty string should return true for nonBlank(String)",
                KjarUtils.nonBlank("there's text in here"));
    }

    /**
     * This test verifies that passing a null string to nonBlank returns false
     */
    @Test
    public void test_nonBlank_false_null() {
        assertFalse("A null string should return false for nonBlank(String)",
                KjarUtils.nonBlank(null));
    }

    /**
     * This test verifies that passing a blank string to nonBlank returns false
     */
    @Test
    public void test_nonBlank_false_emptyString() {
        assertFalse("A string with only whitespace should return false for nonBlank(String)",
                KjarUtils.nonBlank("   "));
    }

    @Test
    public void partialSubmissionTest() {
        Map<VaccineBrand, Integer> full = new HashMap<>();
        full.put(VaccineBrand.JOHNSON, -1);
        full.put(VaccineBrand.JANSSEN, -1);
        full.put(VaccineBrand.PFIZER, 2);
        full.put(VaccineBrand.MODERNA, 2);
        full.put(VaccineBrand.ASTRAZENECA, 2);
        full.put(VaccineBrand.NOVAVAX, 2);

        Map<VaccineBrand, Integer> partial = new HashMap<>();
        partial.put(VaccineBrand.PFIZER, 1);
        partial.put(VaccineBrand.MODERNA, 1);
        partial.put(VaccineBrand.ASTRAZENECA, 1);
        partial.put(VaccineBrand.NOVAVAX, 1);

        for (Entry<VaccineBrand, Integer> e: full.entrySet()) {
            assertFalse(e.getKey() + " shot " + e.getValue(),
                    KjarUtils.partialVaccineSubmission(e.getKey(), e.getValue()));
        }

        for (Entry<VaccineBrand, Integer> e: partial.entrySet()) {
            assertTrue(e.getKey() + " shot " + e.getValue(),
                    KjarUtils.partialVaccineSubmission(e.getKey(), e.getValue()));
        }
    }

    @Test
    public void numberOfDaysElapsedTest() {
        assertEquals(10, KjarUtils.numberOfDaysElapsed(LocalDate.now().minusDays(10)));
        assertEquals(-10, KjarUtils.numberOfDaysElapsed(LocalDate.now().plusDays(10)));
    }
}