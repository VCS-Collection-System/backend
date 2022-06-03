package com.redhat.vcs_kjar;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class CustomeAASMock implements WorkItemHandler {

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        System.out.println("EXECUTING AAS WORK ITEM");
        manager.completeWorkItem(workItem.getId(), null);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        System.out.println("ABORTING AAS WORK ITEM");
        manager.abortWorkItem(workItem.getId());
    }
}
