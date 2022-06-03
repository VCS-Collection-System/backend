package com.redhat.vcs_kjar;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import com.redhat.vcs.model.CovidTestResult;
import com.redhat.vcs.model.CovidTestResultDocument;
import com.redhat.vcs.model.Employee;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.internal.runtime.manager.context.EmptyContext;

public class CovidTestResultSubmissionWorkflowTest extends JbpmJUnitBaseTestCase{

    KieSession ksession = null;
    TaskService taskService = null;
    RuntimeEngine runtimeEngine;
    static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    public CovidTestResultSubmissionWorkflowTest() {

        super(true, true);
    }

    @Before
    public void setUpKieSession() {
        addWorkItemHandler("CustomEmail", new CustomeEmailMock());
       // addWorkItemHandler("Rest", new CustomeAASMock());

        System.setProperty("drools.clockType", "pseudo");
        Map<String, ResourceType> resources = new HashMap<>();
        resources.put("com/redhat/vcs_kjar/covid_test_result_submission_workflow.bpmn", ResourceType.BPMN2);
        resources.put("com/redhat/vcs_kjar/vaccination_card_next_step.drl", ResourceType.DRL);
        createRuntimeManager(resources);
        runtimeEngine = getRuntimeEngine(EmptyContext.get());
        ksession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();
    }
    @After
    public void disposeRunTimeManager() {
        disposeRuntimeManager();
    }

    @Test
    public void positiveTestResult()
    {
        CovidTestResultDocument doc = new CovidTestResultDocument();
        Employee e = new Employee();
        doc.setCovidTestResult(CovidTestResult.POSITIVE);
        doc.setCovidTestDate(LocalDate.of(2022,1,1));
        e.setAgencyCode("ABC");
        e.setAgencyName("USA");
        e.setDivisionCode("div");
        e.setDivisionName("div");
        doc.setEmployee(e);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", doc);
        ProcessInstance processInstance = ksession.startProcess("covid_test_result_submission_workflow",params);
        Long processInstanceId = processInstance.getId();
        //assertEquals(Date.from(doc.getCovidTestDate().plusDays(86).atStartOfDay(DEFAULT_ZONE_ID).toInstant()),((WorkflowProcessInstance) processInstance).getVariable("notificationScheduleDate"));
        assertProcessInstanceCompleted(processInstanceId);

    }

    @Test
    public void negativeTestResult()
    {
        
        CovidTestResultDocument doc = new CovidTestResultDocument();
        Employee e = new Employee();
        doc.setCovidTestResult(CovidTestResult.NEGATIVE);
        doc.setCovidTestDate(LocalDate.of(2022,1,1));
        e.setAgencyCode("ABC");
        e.setAgencyName("USA");
        e.setDivisionCode("div");
        e.setDivisionName("div");
        doc.setEmployee(e);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", doc);
        ProcessInstance processInstance = ksession.startProcess("covid_test_result_submission_workflow",params);
        Long processInstanceId = processInstance.getId();
        //assertEquals(Date.from(doc.getCovidTestDate().plusDays(4).atStartOfDay(DEFAULT_ZONE_ID).toInstant()),((WorkflowProcessInstance) processInstance).getVariable("notificationScheduleDate"));
        assertProcessInstanceNotActive(processInstanceId,ksession);
        assertProcessInstanceCompleted(processInstanceId);

    }

    @Test
    public void inconclusiveTestResult()
    {
        CovidTestResultDocument doc = new CovidTestResultDocument();
        Employee e = new Employee();
        doc.setCovidTestResult(CovidTestResult.INCONCLUSIVE);
        doc.setCovidTestDate(LocalDate.of(2022,1,1));
        e.setAgencyCode("USA");
        e.setAgencyName("USA");
        e.setDivisionCode("div");
        e.setDivisionName("div");
        doc.setEmployee(e);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", doc);
        ProcessInstance processInstance = ksession.startProcess("covid_test_result_submission_workflow",params);
        Long processInstanceId = processInstance.getId();
        assertProcessInstanceNotActive(processInstanceId,ksession);
        assertProcessInstanceCompleted(processInstanceId);

    }

    @Test
    public void recoverySubmission()
    {
        CovidTestResultDocument doc = new CovidTestResultDocument();
        Employee e = new Employee();
        doc.setCovidTestResult(CovidTestResult.RECOVERY);
        doc.setCovidTestDate(LocalDate.of(2022,1,1));
        e.setAgencyCode("DEU");
        e.setAgencyName("DEU");
        e.setDivisionCode("div");
        e.setDivisionName("div");
        doc.setEmployee(e);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", doc);
        ProcessInstance processInstance = ksession.startProcess("covid_test_result_submission_workflow",params);
        Long processInstanceId = processInstance.getId();
        assertProcessInstanceNotActive(processInstanceId,ksession);
        assertProcessInstanceCompleted(processInstanceId);

    }


    
}
