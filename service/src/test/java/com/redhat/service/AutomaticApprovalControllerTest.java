package com.redhat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import com.redhat.vcs.model.ProofType;
import com.redhat.vcs.model.VaxProofAutomaticApprovalResponse;

import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.WorkItemNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * This class contains the unit (logic / flow) tests for AutomaticApprovalController.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
@ExtendWith(MockitoExtension.class)
public class AutomaticApprovalControllerTest {
    @Mock
    private ProcessService processServiceMock;

    @InjectMocks
    private AutomaticApprovalController objectUnderTest_automaticApprovalController;

    /**
     * This "submitReviewResults(Authentication, VaxProofAutomaticApprovalResponse)" test is a happy path validation.
     *     - Provided VaxProofAutomaticApprovalResponse is null
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 200 response code
     *     - Has a null response body
     *
     * The following interactions are verified, in addition to the return value:
     *     - processServerMock.completeWorkItem should be called once
     */
    @Test
    public void test_submitReviewResults_success() {
        // Set up data
        final String ID = "12345";

        VaxProofAutomaticApprovalResponse approvalResponse = new VaxProofAutomaticApprovalResponse();
        approvalResponse.setCorrelationId(ID);
        approvalResponse.setConfidenceScore(200);
        approvalResponse.setHardFail(true);
        approvalResponse.setReport("TPS");
        approvalResponse.setProofType(ProofType.OTHER);

        // Set up mocks

        // Run test
        ResponseEntity<String> response = objectUnderTest_automaticApprovalController
                .submitReviewResults(null, approvalResponse);

        // Validation
        assertEquals(
                HttpStatus.ACCEPTED,
                response.getStatusCode(),
                "Should return HTTP 202 when processing is successful");

        assertNull(
                response.getBody(),
                "The response body should be null on successful submission");

        verify(processServiceMock, times(1)).completeWorkItem(eq(Long.parseLong(ID)),
                eq(Collections.singletonMap("automaticApprovalResponse", approvalResponse)));
    }

    /**
     * This "submitReviewResults(Authentication, VaxProofAutomaticApprovalResponse)" test is an alternate happy path
     * validation for the following condition(s):
     *     - Provided VaxProofAutomaticApprovalResponse is null
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 200 response code
     *     - Has a null response body
     *
     * The following interactions are verified, in addition to the return value:
     *     - processServerMock.completeWorkItem should never be called
     */
    @Test
    public void test_submitReviewResults_success_nullApprovalResponse() {
        // Set up data

        // Set up mocks

        // Run test
        ResponseEntity<String> response = objectUnderTest_automaticApprovalController.submitReviewResults(null, null);

        // Validation
        assertEquals(
                HttpStatus.ACCEPTED,
                response.getStatusCode(),
                "Should return HTTP 202 when processing is skipped");

        assertNull(
                response.getBody(),
                "The response body should be null when processing is skipped");

        verify(processServiceMock, never()).completeWorkItem(anyLong(), anyMap());
    }

    /**
     * This "submitReviewResults(Authentication, VaxProofAutomaticApprovalResponse)" test is an alternate happy path
     * validation, for the following condition(s):
     *     - Provided VaxProofAutomaticApprovalResponse's correlation id is null
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - N/A
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 200 response code
     *     - Has a null response body
     *
     * The following interactions are verified, in addition to the return value:
     *     - processServerMock.completeWorkItem should never be called
     */
    @Test
    public void test_submitReviewResults_success_nullApprovalResponseCorrelationId() {
        // Set up data
        VaxProofAutomaticApprovalResponse approvalResponse = new VaxProofAutomaticApprovalResponse();
        approvalResponse.setCorrelationId(null);

        // Set up mocks

        // Run test
        ResponseEntity<String> response = objectUnderTest_automaticApprovalController
                .submitReviewResults(null, approvalResponse);

        // Validation
        assertEquals(
                HttpStatus.ACCEPTED,
                response.getStatusCode(),
                "Should return HTTP 202 when processing is skipped");

        assertNull(
                response.getBody(),
                "The response body should be null when processing is skipped");

        verify(processServiceMock, never()).completeWorkItem(anyLong(), anyMap());
    }

    /**
     * This "submitReviewResults(Authentication, VaxProofAutomaticApprovalResponse)" test is a negative path
     * validation, for the following condition(s):
     *     - AuthorizationException thrown during processing
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - Throw AuthorizationException when processServiceMock.completeWorkItem is called
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 403 response code
     *     - Contains exception message in the response body
     */
    @Test
    public void test_submitReviewResults_fail_authorizationException() {
        // Set up data
        final String ERROR = "Working hard, or hardly working?";

        VaxProofAutomaticApprovalResponse approvalResponse = new VaxProofAutomaticApprovalResponse();
        approvalResponse.setCorrelationId("12345");

        // Set up mocks
        doThrow(new AuthorizationException(ERROR))
                .when(processServiceMock).completeWorkItem(anyLong(), anyMap());

        // Run test
        ResponseEntity<String> response = objectUnderTest_automaticApprovalController
                .submitReviewResults(null, approvalResponse);

        // Validation
        assertEquals(
                HttpStatus.FORBIDDEN,
                response.getStatusCode(),
                "Should return HTTP 403 when authorization fails");

        assertEquals(
                ERROR,
                response.getBody(),
                "The response body should contain the exception message when authorization fails");
    }

    /**
     * This "submitReviewResults(Authentication, VaxProofAutomaticApprovalResponse)" test is a negative path
     * validation, for the following condition(s):
     *     - Any Exception (excluding AuthorizationException) thrown during processing
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - Throw WorkItemNotFoundException when processServiceMock.completeWorkItem is called
     *
     * Calling the method under test should return a ResponseEntity<String> which:
     *     - Contains a 400 response code
     *     - Contains exception message in the response body
     */
    @Test
    public void test_submitReviewResults_fail_miscException() {
        // Set up data
        final String ERROR = "I'm sorry, Dave. I'm afraid I can't do that.";

        VaxProofAutomaticApprovalResponse approvalResponse = new VaxProofAutomaticApprovalResponse();
        approvalResponse.setCorrelationId("12345");

        // Set up mocks
        doThrow(new WorkItemNotFoundException(ERROR))
                .when(processServiceMock).completeWorkItem(anyLong(), anyMap());

        // Run test
        ResponseEntity<String> response = objectUnderTest_automaticApprovalController
                .submitReviewResults(null, approvalResponse);

        // Validation
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Should return HTTP 400 when processing fails");

        assertEquals(
                ERROR,
                response.getBody(),
                "The response body should contain the exception message when authorization fails");
    }
}
