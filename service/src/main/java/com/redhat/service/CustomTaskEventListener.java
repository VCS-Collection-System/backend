package com.redhat.service;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.redhat.vcs.model.Document;
import com.redhat.vcs.model.DocumentTaskMapping;

import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.TaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CustomTaskEventListener implements TaskLifeCycleEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(CustomTaskEventListener.class);

    private static final String DOCUMENT_KEY = "document";

    @PersistenceContext
    EntityManager em;

    @Transactional
    private void saveMapping(TaskEvent event) {
        Long taskId = event.getTask().getId();
        TaskData taskData = event.getTask().getTaskData();

        if (taskData != null
                && taskData.getTaskInputVariables() != null
                && taskData.getTaskInputVariables().containsKey(DOCUMENT_KEY)) {
            long pid = taskData.getProcessInstanceId();
            Document document = (Document) taskData.getTaskInputVariables().get(DOCUMENT_KEY);
            long documentId = document.getId();

            DocumentTaskMapping mapping = new DocumentTaskMapping();
            mapping.setDocumentId(documentId);
            mapping.setTaskId(taskId);
            mapping.setProcessInstanceId(pid);

            em.persist(mapping);
            LOG.debug(">>> Persisting {}", mapping);
        }
    }

    @Override
    public void afterTaskActivatedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskAddedEvent(TaskEvent event) {
        LOG.info(">>> After task added. {}", event);
        saveMapping(event);
    }

    @Override
    public void afterTaskAssignmentsAddedEvent(TaskEvent event, AssignmentType type,
            List<OrganizationalEntity> entities) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskAssignmentsRemovedEvent(TaskEvent event, AssignmentType type,
            List<OrganizationalEntity> entities) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskClaimedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskCompletedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskDelegatedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskExitedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskFailedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskForwardedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskInputVariableChangedEvent(TaskEvent event, Map<String, Object> variables) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskNominatedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskNotificationEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskOutputVariableChangedEvent(TaskEvent event, Map<String, Object> variables) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskReassignedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskReleasedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskResumedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskSkippedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskStartedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskStoppedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskSuspendedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void afterTaskUpdatedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskActivatedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskAddedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskAssignmentsAddedEvent(TaskEvent event, AssignmentType type,
            List<OrganizationalEntity> entities) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskAssignmentsRemovedEvent(TaskEvent event, AssignmentType type,
            List<OrganizationalEntity> entities) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskClaimedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskCompletedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskDelegatedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskExitedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskFailedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskForwardedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskInputVariableChangedEvent(TaskEvent event, Map<String, Object> variables) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskNominatedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskNotificationEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskOutputVariableChangedEvent(TaskEvent event, Map<String, Object> variables) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskReassignedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskReleasedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskResumedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskSkippedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskStartedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskStoppedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskSuspendedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }

    @Override
    public void beforeTaskUpdatedEvent(TaskEvent event) {
        // No-op; must override this method due to TaskLifeCycleEventListener interface
    }
}
