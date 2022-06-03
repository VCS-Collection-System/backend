package com.redhat.vcs.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * This class contains the unit (logic / flow) tests for VaccineDocumentDump.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
public class VaccineDocumentDumpTest {
    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the VaccineDocumentDump
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final Long ID = 5L;
        final String EMP_ID = "Jim Darkmagic";
        final String AGENCY_CODE = "TEE";
        final String AGENCY_NAME = "The Emerald Enclave";
        final LocalDate LAST_DOSE_DATE = LocalDate.of(2014, 9, 30);
        final VaccineBrand VACCINE_BRAND = VaccineBrand.MODERNA;

        VaccineDocumentDump vaccineDocumentDump = new VaccineDocumentDump();

        vaccineDocumentDump.setId(ID);
        vaccineDocumentDump.setEmployeeId(EMP_ID);
        vaccineDocumentDump.setAgencyCode(AGENCY_CODE);
        vaccineDocumentDump.setAgencyName(AGENCY_NAME);
        vaccineDocumentDump.setLastDoseDate(LAST_DOSE_DATE);
        vaccineDocumentDump.setVaccineBrand(VACCINE_BRAND);

        final String EXPECTED_VAX_DOC_DUMP_REGEX = "VaccineDocumentDump \\["
                + "agencyCode=" + AGENCY_CODE
                + ", agencyName=" + AGENCY_NAME
                + ", employeeId=" + "\\*+"
                + ", id=" + ID
                + ", lastDoseDate=" + LAST_DOSE_DATE
                + ", vaccineBrand=" + VACCINE_BRAND
                + "\\]";

        // Set up mocks

        // Run test
        String vaccineDocumentDumpString = vaccineDocumentDump.toString();

        // Validate
        assertTrue(
                vaccineDocumentDumpString.matches(EXPECTED_VAX_DOC_DUMP_REGEX),
                "VaccineDocumentDump String does not match the expected format");
    }
}
