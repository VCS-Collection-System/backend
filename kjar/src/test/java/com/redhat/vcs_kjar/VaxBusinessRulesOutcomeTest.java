package com.redhat.vcs_kjar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * This class contains the unit (logic / flow) tests for VaxBusinessRulesOutcome.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 *
 * Note: Due to the (unenforced) contract between equals and hashCode, both methods will be tested together.
 */
public class VaxBusinessRulesOutcomeTest {
    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the VaxBusinessRulesOutcome
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final String ISSUE1 = "Whoops";
        final String ISSUE2 = "Uh oh";

        final boolean FULLY_VACCINATED = false;
        final LocalDate FULL_VAX_DATE = LocalDate.of(2020, 3, 15);
        final List<String> ISSUE_LIST = Arrays.asList(ISSUE1, ISSUE2);

        VaxBusinessRulesOutcome vaxBusinessRulesOutcome = new VaxBusinessRulesOutcome();
        vaxBusinessRulesOutcome.setFullVaccinatedDate(FULL_VAX_DATE);
        vaxBusinessRulesOutcome.setFullyVaccinated(FULLY_VACCINATED);
        vaxBusinessRulesOutcome.setIssueList(ISSUE_LIST);

        final String EXPECTED_OUTCOME_STRING = "VaxBusinessRulesOutcome ["
                + "fullVaccinatedDate=" + FULL_VAX_DATE
                + ", fullyVaccinated=" + FULLY_VACCINATED
                + ", issueList=" + ISSUE_LIST
                + "]";

        // Set up mocks

        // Run test
        String approvalString = vaxBusinessRulesOutcome.toString();

        // Validate
        assertEquals(
                EXPECTED_OUTCOME_STRING,
                approvalString,
                "VaxBusinessRulesOutcome String does not match the expected format");
    }
}
