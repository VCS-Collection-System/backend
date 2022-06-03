package com.redhat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import com.redhat.vcs.model.CovidTestResultDocument;
import com.redhat.vcs.model.Document;
import com.redhat.vcs.model.DocumentTaskMapping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * This class contains the unit (logic / flow) tests for NotificationUtils.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 *
 * Note: Due to the TaskLifeCycleEventListener, numerous methods must be implemented, even if they are a no-op; there
 *       are several no-op tests below to match this behavior.
 */
@ExtendWith(MockitoExtension.class)
public class CustomTaskEventListenerTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private CustomTaskEventListener objectUnderTest_customTaskEventListener;

    // Used to capture a DocumentTaskMapping that is passed into em.persist, for inspection
    @Captor
    private ArgumentCaptor<DocumentTaskMapping> docTaskMappingCaptor;

    /**
     * This "afterTaskAddedEvent()" test is a happy path validation.
     *
     * To ensure this workflow, the following mock behavior(s) will be set up:
     *     - Task, TaskEvent, and TaskData are mocked to set up the correct data
     *
     * NOTE: Since there is no return value, this test instead captures the object passed to em.merge to ensure the
     * values have been set as expected.
     *
     * The following interactions are verified, in lieu of a return value:
     *     - em.persist should be called once
     *     - The DocumentTaskMapping passed into em.persist has the correct value for the following:
     *         - processInstanceId
     *         - documentId
     *         - taskId
     */
    @Test
    public void test_afterTaskAddedEvent_success() {
        // Set up data
        final Long TASK_ID = 10000L;
        final Long PIID = 20000L;
        final Long DOC_ID = 30000L;

        Document document = new CovidTestResultDocument();
        document.setId(DOC_ID);

        Map<String, Object> inputVars = new HashMap<>();

        inputVars.put("document", document);

        // Set up mocks
        TaskEvent event = mock(TaskEvent.class);
        Task task = mock(Task.class);
        TaskData taskData = mock(TaskData.class);

        when(event.getTask())
                .thenReturn(task);

        when(task.getId())
                .thenReturn(TASK_ID);
        when(task.getTaskData())
                .thenReturn(taskData);

        when(taskData.getProcessInstanceId())
                .thenReturn(PIID);
        when(taskData.getTaskInputVariables())
                .thenReturn(inputVars);

        // Run test
        objectUnderTest_customTaskEventListener.afterTaskAddedEvent(event);

        // Capture the DocumentTaskMapping passed into em.persist:
        verify(em).persist(docTaskMappingCaptor.capture());
        DocumentTaskMapping documentTaskMapping = docTaskMappingCaptor.getValue();

        // Validate
        verify(em, times(1)).persist(any(DocumentTaskMapping.class));

        assertEquals(
                DOC_ID,
                documentTaskMapping.getDocumentId(),
                "Document ID not set correctly in captured DocumentTaskMapping");

        assertEquals(
                TASK_ID,
                documentTaskMapping.getTaskId(),
                "Task ID not set correctly in captured DocumentTaskMapping");

        assertEquals(
                PIID,
                documentTaskMapping.getProcessInstanceId(),
                "Process Instance ID not set correctly in captured DocumentTaskMapping");
    }

    /*
     * No-op tests below!
     */

    @Test
    public void test_afterTaskActivatedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskActivatedEvent(null);
    }

    @Test
    public void test_afterTaskAssignmentsAddedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskAssignmentsAddedEvent(null, null,null);
    }

    @Test
    public void test_afterTaskAssignmentsRemovedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskAssignmentsRemovedEvent(null, null, null);
    }

    @Test
    public void test_afterTaskClaimedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskClaimedEvent(null);
    }

    @Test
    public void test_afterTaskCompletedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskCompletedEvent(null);
    }

    @Test
    public void test_afterTaskDelegatedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskDelegatedEvent(null);
    }

    @Test
    public void test_afterTaskExitedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskExitedEvent(null);
    }

    @Test
    public void test_afterTaskFailedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskFailedEvent(null);
    }

    @Test
    public void test_afterTaskForwardedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskForwardedEvent(null);
    }

    @Test
    public void test_afterTaskInputVariableChangedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskInputVariableChangedEvent(null, null);
    }

    @Test
    public void afterTaskNominatedEvent() {
        objectUnderTest_customTaskEventListener.afterTaskActivatedEvent(null);
    }

    @Test
    public void test_afterTaskNotificationEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskNotificationEvent(null);
    }

    @Test
    public void test_afterTaskOutputVariableChangedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskOutputVariableChangedEvent(null, null);
    }

    @Test
    public void test_afterTaskReassignedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskReassignedEvent(null);
    }

    @Test
    public void test_afterTaskReleasedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskReleasedEvent(null);
    }

    @Test
    public void test_afterTaskResumedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskResumedEvent(null);
    }

    @Test
    public void test_afterTaskSkippedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskSkippedEvent(null);
    }

    @Test
    public void test_afterTaskStartedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskStartedEvent(null);
    }

    @Test
    public void test_afterTaskStoppedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskStoppedEvent(null);
    }

    @Test
    public void test_afterTaskSuspendedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskSuspendedEvent(null);
    }

    @Test
    public void test_afterTaskUpdatedEvent_noop() {
        objectUnderTest_customTaskEventListener.afterTaskUpdatedEvent(null);
    }

    @Test
    public void test_beforeTaskActivatedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskActivatedEvent(null);
    }

    @Test
    public void test_beforeTaskAddedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskAddedEvent(null);
    }

    @Test
    public void test_beforeTaskAssignmentsAddedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskAssignmentsAddedEvent(null, null, null);
    }

    @Test
    public void test_beforeTaskAssignmentsRemovedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskAssignmentsRemovedEvent(null, null, null);
    }

    @Test
    public void test_beforeTaskClaimedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskClaimedEvent(null);
    }

    @Test
    public void test_beforeTaskCompletedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskCompletedEvent(null);
    }

    @Test
    public void test_beforeTaskDelegatedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskDelegatedEvent(null);
    }

    @Test
    public void test_beforeTaskExitedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskExitedEvent(null);
    }

    @Test
    public void test_beforeTaskFailedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskFailedEvent(null);
    }

    @Test
    public void test_beforeTaskForwardedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskForwardedEvent(null);
    }

    @Test
    public void test_beforeTaskInputVariableChangedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskInputVariableChangedEvent(null, null);
    }

    @Test
    public void test_beforeTaskNominatedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskNominatedEvent(null);
    }

    @Test
    public void test_beforeTaskNotificationEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskNotificationEvent(null);
    }

    @Test
    public void test_beforeTaskOutputVariableChangedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskOutputVariableChangedEvent(null, null);
    }

    @Test
    public void test_beforeTaskReassignedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskReassignedEvent(null);
    }

    @Test
    public void test_beforeTaskReleasedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskReleasedEvent(null);
    }

    @Test
    public void test_beforeTaskResumedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskResumedEvent(null);
    }

    @Test
    public void test_beforeTaskSkippedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskSkippedEvent(null);
    }

    @Test
    public void test_beforeTaskStartedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskStartedEvent(null);
    }

    @Test
    public void test_beforeTaskStoppedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskStoppedEvent(null);
    }

    @Test
    public void test_beforeTaskSuspendedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskSuspendedEvent(null);
    }

    @Test
    public void test_beforeTaskUpdatedEvent_noop() {
        objectUnderTest_customTaskEventListener.beforeTaskUpdatedEvent(null);
    }
}
