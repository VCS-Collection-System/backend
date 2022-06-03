package com.redhat.vcs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * This class contains the unit (logic / flow) tests for DocumentTaskMapping.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
public class DocumentTaskMappingTest {
    /**
     * This "toString()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a String which:
     *     - Contains all of the data from the DocumentTaskMapping
     */
    @Test
    public void test_toString_success() {
        // Set up data
        final Long ID = 1L;
        final Long DOC_ID = 100L;
        final Long TASK_ID = 10000L;
        final Long PI_ID = 1000000L;

        DocumentTaskMapping documentTaskMapping = new DocumentTaskMapping();

        documentTaskMapping.setId(ID);
        documentTaskMapping.setDocumentId(DOC_ID);
        documentTaskMapping.setTaskId(TASK_ID);
        documentTaskMapping.setProcessInstanceId(PI_ID);

        final String EXPECTED_DOC_TASK_MAPPING_STRING = "DocumentTaskMapping ["
                + "id=" + ID
                + ", documentId=" + DOC_ID
                + ", taskId=" + TASK_ID
                + ", processInstanceId=" + PI_ID
                + "]";

        // Set up mocks

        // Run test
        String docTaskMappingString = documentTaskMapping.toString();

        // Validate
        assertEquals(
                EXPECTED_DOC_TASK_MAPPING_STRING,
                docTaskMappingString,
                "DocumentTaskMapping String does not match the expected format");
    }
}
