package com.redhat.service.cmd;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.executor.CommandContext;

public class ProcessReplayCommandTest {

    private static final String DRY_RUN_KEY = "dryRun";
    ProcessReplayCommand testObject;
    
    @BeforeEach
    public void setup() {
        testObject = new ProcessReplayCommand();
    }
    
    /** Tests that method is null safe handling the list. */
    @Test
    public void testProcessInListNull() {
        assertFalse(testObject.processInList(null, null));
    }
    
    /** Tests that the method handles empty lists.  Not sure why we need this, but not touching it. */
    @Test
    public void testProcessInListEmpty() {
        assertFalse(testObject.processInList(new ArrayList<Long>(), null));
    }
    
    /** Tests that the method does not find the process instance if in the list. */
    @Test
    public void testProcessInListNotFound() {
        Long id = Long.valueOf(10);
        ArrayList<Long> processInstanceList = new ArrayList<Long>();
        WorkflowProcessInstanceImpl piMock = mock(WorkflowProcessInstanceImpl.class);
        when(piMock.getId()).thenReturn(Long.valueOf(11));
        processInstanceList.add(id);
        
        assertFalse(testObject.processInList(processInstanceList, piMock));
    }
    
    /** Tests that the method finds the process instance in the list. */
    @Test
    public void testProcessInListFound() {
        Long id = Long.valueOf(10);
        ArrayList<Long> processInstanceList = new ArrayList<Long>();
        WorkflowProcessInstanceImpl piMock = mock(WorkflowProcessInstanceImpl.class);
        when(piMock.getId()).thenReturn(id);
        processInstanceList.add(id);
        
        assertTrue(testObject.processInList(processInstanceList, piMock));
    }
    
    /** Tests this method properly determines the pi DateTime is before the start DateTime. */
    @Test
    public void testDateOutsideRangeBeforeStart() {
        LocalDateTime startDateTime = LocalDateTime.of(1970, 1, 1, 6, 0);
        LocalDateTime piDateTime = LocalDateTime.of(1970, 1, 1, 1, 0);
        
        assertTrue(testObject.dateOutsideRange(startDateTime, null, piDateTime));
    }
    
    /** Tests this method properly determines the pi DateTime is after end DateTime. */
    @Test
    public void testDateOutsideRangeAfterEnd() {
        LocalDateTime startDateTime = LocalDateTime.of(1970, 1, 1, 1, 0);
        LocalDateTime endDateTime = LocalDateTime.of(1970, 1, 1, 12, 0);
        LocalDateTime piDateTime = LocalDateTime.of(1970, 1, 1, 14, 0);
        
        assertTrue(testObject.dateOutsideRange(startDateTime, endDateTime, piDateTime));
    }
    
    /** Tests this method properly determines the pi DateTime is within DateTime range. */
    @Test
    public void testDateWithinRange() {
        LocalDateTime startDateTime = LocalDateTime.of(1970, 1, 1, 1, 0);
        LocalDateTime endDateTime = LocalDateTime.of(1970, 1, 1, 12, 0);
        LocalDateTime piDateTime = LocalDateTime.of(1970, 1, 1, 6, 0);
        
        assertFalse(testObject.dateOutsideRange(startDateTime, endDateTime, piDateTime));
    }
    
    /** Tests this method is null safe when checking context value; defaults to true. */
    @Test
    public void testIsDryRunNullSafe() {
        CommandContext cmdCtxMock = mock(CommandContext.class);
        when(cmdCtxMock.getData(DRY_RUN_KEY)).thenReturn(null);
        assertTrue(testObject.isDryRun(cmdCtxMock));
    }
    
    /** Tests this method returns false when "dryRun" value in context is set to false. */
    @Test
    public void testIsDryRunFalse() {
        CommandContext cmdCtxMock = mock(CommandContext.class);
        when(cmdCtxMock.getData(DRY_RUN_KEY)).thenReturn(false);
        assertFalse(testObject.isDryRun(cmdCtxMock));
    }
    
    /** Tests this method returns true when "dryRun" value in context is set to true. */
    @Test
    public void testIsDryRunTrue() {
        CommandContext cmdCtxMock = mock(CommandContext.class);
        when(cmdCtxMock.getData(DRY_RUN_KEY)).thenReturn(true);
        assertTrue(testObject.isDryRun(cmdCtxMock));
    }
}
