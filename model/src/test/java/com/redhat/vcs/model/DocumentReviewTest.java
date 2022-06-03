package com.redhat.vcs.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * This class contains the unit (logic / flow) tests for DocumentReview.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
public class DocumentReviewTest {
    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the DocumentReview
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final Long SIZE = 123456L;
        final String REVIEWER_EMP_ID = "006";
        final String REVIEWER_NOTES = "This person is an idiot sandwich >:";
        final String REJECT_REASON = "I'd prefer an idiot salad";
        final LocalDateTime REVIEW_DATE = LocalDateTime.now();
        final DocumentReviewOutcome OUTCOME = DocumentReviewOutcome.DECLINED;

        DocumentReview documentReview = new DocumentReview();

        documentReview.setReviewerNotes(REVIEWER_NOTES);
        documentReview.setReviewerEmployeeId(REVIEWER_EMP_ID);
        documentReview.setRejectReason(REJECT_REASON);
        documentReview.setReviewDate(REVIEW_DATE);
        documentReview.setReviewOutcome(OUTCOME);

        final String EXPECTED_DOC_REVIEW_REGEX = "DocumentReview \\["
                + "reviewerEmployeeId=" + "\\*+"
                + ", reviewerNotes=" + REVIEWER_NOTES
                + ", reviewDate=" + REVIEW_DATE
                + ", reviewOutcome=" + OUTCOME
                + ", rejectReason=" + REJECT_REASON
                + "\\]";

        // Set up mocks

        // Run test
        String documentReviewString = documentReview.toString();

        // Validate
        assertTrue(
                documentReviewString.matches(EXPECTED_DOC_REVIEW_REGEX),
                "DocumentReview String does not match the expected format");
    }
}
