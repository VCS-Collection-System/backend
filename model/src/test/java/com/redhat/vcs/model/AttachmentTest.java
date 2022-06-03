package com.redhat.vcs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * This class contains the unit (logic / flow) tests for Attachment.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
public class AttachmentTest {
    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the Attachment
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final Long SIZE = 123456L;
        final String ORIGINAL_FILE_NAME = "lolcat.jpg";
        final String CONTENT_TYPE = "image/adorable";
        final String BUCKET_NAME = "Mr. Bucket";
        final String UUID = "Druid";

        Attachment attachment = new Attachment();

        attachment.setSize(SIZE);
        attachment.setS3BucketName(BUCKET_NAME);
        attachment.setContentType(CONTENT_TYPE);
        attachment.setS3UUID(UUID);
        attachment.setOriginalFileName(ORIGINAL_FILE_NAME);

        final String EXPECTED_ATTACHMENT_STRING = "Attachment ["
                + "contentType=" + CONTENT_TYPE
                + ", originalFileName=" + ORIGINAL_FILE_NAME
                + ", s3BucketName=" + BUCKET_NAME
                + ", s3UUID=" + UUID
                + ", size=" + SIZE
                + "]";

        // Set up mocks

        // Run test
        String attachmentString = attachment.toString();

        // Validate
        assertEquals(
                EXPECTED_ATTACHMENT_STRING,
                attachmentString,
                "Attachment String does not match the expected format");
    }
}
