package com.redhat.service.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import com.redhat.service.cmd.EmailNotificationCommand;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * This class contains the unit (logic / flow) tests for CustomEmailWorkItemHandler.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 */
@ExtendWith(MockitoExtension.class)
public class CustomEmailWorkItemHandlerTest {
    @Mock
    private ExecutorService executorServiceMock;

    @InjectMocks
    private CustomEmailWorkItemHandler objectUnderTest_customEmailWorkItemHandler;

    @Captor
    private ArgumentCaptor<CommandContext> commandContextCaptor;

    @Test
    public void test_executeWorkItem_success(){
        // Set up data
        // Set up mocks
        // Run test
        // Validate
    }

    @Test
    public void test_executeWorkItem_success_recipMissingAndIsHrNotif() {
        // Set up data
        final long ID = 1L;

        // Set up mocks
        WorkItemManager workItemManagerMock = mock(WorkItemManager.class);
        WorkItem workItemMock = mock(WorkItem.class);

        when(workItemMock.getParameter("Template"))
                .thenReturn(EmailTemplates.POSITIVE_RESULT_HR);
        when(workItemMock.getParameter("Recipients"))
                .thenReturn(null);
        when(workItemMock.getId())
                .thenReturn(ID);

        // Run test
        objectUnderTest_customEmailWorkItemHandler.executeWorkItem(workItemMock, workItemManagerMock);

        // Validate
        verify(workItemManagerMock, times(1))
                .completeWorkItem(ID, null);

        verify(executorServiceMock, never())
                .scheduleRequest(anyString(), any(Date.class), any(CommandContext.class));
    }

    @Test
    public void test_executeWorkItem_success_recipMissingAndIsNotHrNotif() {
        // Set up data
        final long ID = 1L;

        // Set up mocks
        WorkItemManager workItemManagerMock = mock(WorkItemManager.class);
        WorkItem workItemMock = mock(WorkItem.class);

        when(workItemMock.getParameter("Template"))
                .thenReturn(EmailTemplates.VACCINE_UNDER_REVIEW);
        when(workItemMock.getParameter("Recipients"))
                .thenReturn("test@example.org");
        when(workItemMock.getParameter("ScheduleDate"))
                .thenReturn(null);
        when(workItemMock.getId())
                .thenReturn(ID);

        when(workItemMock.getParameter("TemplateParam1"))
                .thenReturn("Param1");
        when(workItemMock.getParameter("TemplateParam2"))
                .thenReturn("Param2");
        when(workItemMock.getParameter("TemplateParam3"))
                .thenReturn("Param3");
        when(workItemMock.getParameter("TemplateParam4"))
                .thenReturn("Param4");
        when(workItemMock.getParameter("TemplateParam5"))
                .thenReturn("Param5");

        // Run test
        objectUnderTest_customEmailWorkItemHandler.executeWorkItem(workItemMock, workItemManagerMock);

        // Capture the CommandContext passed into executorService.scheduleRequest:
        verify(executorServiceMock, times(1))
                .scheduleRequest(eq(EmailNotificationCommand.class.getName()), any(Date.class),
                        commandContextCaptor.capture());
        CommandContext commandContext = commandContextCaptor.getValue();

        // Validate
        assertNotNull(
                commandContext,
                "The captured CommandContext should not be null");

        assertEquals(
                "Vaccination Submission Under Review",
                commandContext.getData("subject"),
                "CommandContext subject was not set correctly");

        verify(workItemManagerMock, times(1))
                .completeWorkItem(ID, null);
    }

    /**
     * TODO: Testing the "handleException" method from the superclass is difficult.
     */
    @Disabled
    @Test
    public void test_executeWorkItem_fail_templateNotFound() {
    }
}
