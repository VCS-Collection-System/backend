package com.redhat.vcs_kjar;



import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.redhat.vcs.model.DocumentReview;
import com.redhat.vcs.model.DocumentReviewOutcome;
import com.redhat.vcs.model.Employee;
import com.redhat.vcs.model.ProofType;
import com.redhat.vcs.model.VaccineBrand;
import com.redhat.vcs.model.VaccineCardDocument;
import com.redhat.vcs.model.VaccineDocumentList;
import com.redhat.vcs.model.VaxProofAutomaticApprovalRequest;
import com.redhat.vcs.model.VaxProofAutomaticApprovalResponse;

import org.drools.core.time.impl.PseudoClockScheduler;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.manager.context.EmptyContext;


/**
 * This class contains the unit (logic / flow) tests for Vax_card_aas_review_workflow.bpm
 *
 * For strict unit testing, the following will be mocked:
 *     - All work item handlers, test data, tasks.
 *
 */
public class VaxCardAasReviewWorkflowTest extends JbpmJUnitBaseTestCase implements Serializable{
    KieSession ksession = null;
    TaskService taskService = null;

    public VaxCardAasReviewWorkflowTest() {

        super(true, true);
    }

    @Before
    public void setUpKieSession() {
        addWorkItemHandler("CustomEmail", new CustomeEmailMock());
        addWorkItemHandler("Rest", new CustomeAASMock());

        System.setProperty("drools.clockType", "pseudo");
        Map<String, ResourceType> resources = new HashMap<>();
        resources.put("com/redhat/vcs_kjar/vax_card_aas_review_workflow.bpmn", ResourceType.BPMN2);
        resources.put("com/redhat/vcs_kjar/vaccination_card_next_step.drl", ResourceType.DRL);
        createRuntimeManager(resources);
        RuntimeEngine runtimeEngine = getRuntimeEngine(EmptyContext.get());
        ksession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();
    }

    @After
    public void disposeRunTimeManager() {
        disposeRuntimeManager();
    }

    @Test
    public void testSubmittedByIsNotNull()
    {
        VaccineCardDocument doc = new VaccineCardDocument();
        Employee e = new Employee();
        doc.setSubmittedBy("abc");
        doc.setProofType(ProofType.SMARTHEALTH);
        doc.setVaccineBrand(VaccineBrand.PFIZER);
        doc.setVaccineShotNumber(1);
        doc.setEmployee(e);
        ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<>();
        vaccineCardDocuments.add(doc);
        VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
        vaccineDocumentList.setDocuments(vaccineCardDocuments);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("documentList", vaccineDocumentList);
        ProcessInstance processInstance = ksession.startProcess("vax_card_aas_review_workflow",params);
        Long processInstanceId = processInstance.getId();
        assertProcessVarExists(processInstance,"employeeEmails" );
        assertNodeTriggered(processInstanceId,"SET VARIABLES");
        assertProcessInstanceNotActive(processInstanceId,ksession);
        assertProcessInstanceCompleted(processInstanceId);

    }

    @Test
    public void partialSubmission()
    {
        VaccineCardDocument doc = new VaccineCardDocument();
        Employee e = new Employee();
        doc.setProofType(ProofType.SMARTHEALTH);
        doc.setVaccineBrand(VaccineBrand.PFIZER);
        doc.setVaccineShotNumber(1);
        doc.setEmployee(e);

        ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<>();
        vaccineCardDocuments.add(doc);
        VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
        vaccineDocumentList.setDocuments(vaccineCardDocuments);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", doc);
        params.put("documentList", vaccineDocumentList);
        ProcessInstance processInstance = ksession.startProcess("vax_card_aas_review_workflow",params);
        Long processInstanceId = processInstance.getId();
        assertNodeTriggered(processInstanceId,"SET VARIABLES","NOTIFY EMPLOYEE - PARTIAL SUBMISSION");
        assertProcessInstanceNotActive(processInstanceId,ksession);
        assertProcessInstanceCompleted(processInstanceId);
    }
    @Test
    public void autoApprovalNotApplicableDeclined()
    {
        VaccineCardDocument doc = new VaccineCardDocument();
        Employee e = new Employee();
        doc.setProofType(ProofType.CDC);
        doc.setVaccineBrand(VaccineBrand.PFIZER);
        doc.setVaccineShotNumber(1);
        doc.setEmployee(e);
        doc.setVaccineAdministrationDate(LocalDate.of(2020,1,1));
        VaccineCardDocument doc2 = new VaccineCardDocument();
        doc2.setProofType(ProofType.CDC);
        doc2.setVaccineBrand(VaccineBrand.PFIZER);
        doc2.setVaccineShotNumber(2);
        doc2.setEmployee(e);
        doc2.setVaccineAdministrationDate(LocalDate.of(2021,1,1));
        ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<>();
        vaccineCardDocuments.add(doc);
        vaccineCardDocuments.add(doc2);
        VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
        vaccineDocumentList.setDocuments(vaccineCardDocuments);
        Boolean aasEnabled = false;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", doc);
        params.put("documentList", vaccineDocumentList);
        params.put("aasEnabled",aasEnabled);
        params.put("vaxProofAutomaticApprovalRequest", new VaxProofAutomaticApprovalRequest());
        ProcessInstance processInstance = ksession.startProcess("vax_card_aas_review_workflow",params);
        Long processInstanceId = processInstance.getId();
        assertProcessVarExists(processInstance,"automaticApproval" );
        DocumentReview documentReview = new DocumentReview();
        documentReview.setReviewOutcome(DocumentReviewOutcome.DECLINED);
        documentReview.setRejectReason("rejectReason");
        List<Long> items = taskService.getTasksByProcessInstanceId(processInstanceId);
        Task task = taskService.getTaskById(items.get(0));
        taskService.start(task.getId(), "Administrator");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("documentReview", documentReview);
        taskService.complete(task.getId(), "Administrator", results);
        assertProcessVarExists(processInstance,"document" );
        assertNodeTriggered(processInstanceId,"SET VARIABLES","NOTIFY EMPLOYEE - UNDER REVIEW","AUTOMATIC APPROVAL APPLICABLE","REVIEW VACCINATION CARD","NOTIFY EMPLOYEE - DECLINED");
        assertProcessInstanceNotActive(processInstanceId,ksession);
        assertProcessInstanceCompleted(processInstanceId);

    }

    @Test
    public void autoApprovalNotApplicableAccepted()
    {
        VaccineCardDocument doc1 = new VaccineCardDocument();
        Employee e = new Employee();
        doc1.setProofType(ProofType.CDC);
        doc1.setVaccineBrand(VaccineBrand.PFIZER);
        doc1.setVaccineShotNumber(1);
        doc1.setEmployee(e);
        doc1.setVaccineAdministrationDate(LocalDate.of(2020,1,1));
        VaccineCardDocument doc2 = new VaccineCardDocument();
        doc2.setProofType(ProofType.CDC);
        doc2.setVaccineBrand(VaccineBrand.PFIZER);
        doc2.setVaccineShotNumber(2);
        doc2.setEmployee(e);
        doc2.setVaccineAdministrationDate(LocalDate.of(2021,1,1));
        ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<>();
        vaccineCardDocuments.add(doc1);
        vaccineCardDocuments.add(doc2);
        VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
        vaccineDocumentList.setDocuments(vaccineCardDocuments);
        Boolean aasEnabled = false;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", doc1);
        params.put("documentList", vaccineDocumentList);
        params.put("aasEnabled",aasEnabled);
        params.put("vaxProofAutomaticApprovalRequest", new VaxProofAutomaticApprovalRequest());
        ProcessInstance processInstance = ksession.startProcess("vax_card_aas_review_workflow",params);
        Long processInstanceId = processInstance.getId();
        assertProcessVarExists(processInstance,"automaticApproval" );
        DocumentReview documentReview = new DocumentReview();
        documentReview.setReviewOutcome(DocumentReviewOutcome.ACCEPTED);
        List<Long> items = taskService.getTasksByProcessInstanceId(processInstanceId);
        Task task = taskService.getTaskById(items.get(0));
        // System.out.println("mary starting task " + task.getPeopleAssignments().getPotentialOwners() + task.getPeopleAssignments().getBusinessAdministrators());
        taskService.start(task.getId(), "Administrator");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("documentReview", documentReview);
        taskService.complete(task.getId(), "Administrator", results);
        assertProcessVarExists(processInstance,"document" );
        assertNodeTriggered(processInstanceId,"SET VARIABLES","NOTIFY EMPLOYEE - UNDER REVIEW","AUTOMATIC APPROVAL APPLICABLE","REVIEW VACCINATION CARD","NEXT STEP","SET FULLY VACCINATED FLAG","NOTIFY EMPLOYEE - ACCEPTED");
        assertProcessInstanceNotActive(processInstanceId,ksession);
        assertProcessInstanceCompleted(processInstanceId);

    }

    @Test
    public void autoApprovalServiceTimerThresholdMetFullyVaccinated()
    {
        //TODO try to assert the values in scripts
        VaccineCardDocument doc = new VaccineCardDocument();
        Employee e = new Employee();
        doc.setVaccineBrand(VaccineBrand.PFIZER);
        doc.setVaccineAdministrationDate(LocalDate.of(2020,1,1));
        doc.setVaccineShotNumber(1);
        doc.setEmployee(e);
        doc.setProofType(ProofType.CDC);
        VaccineCardDocument doc2 = new VaccineCardDocument();
        doc2.setProofType(ProofType.CDC);
        doc2.setVaccineBrand(VaccineBrand.PFIZER);
        doc2.setVaccineShotNumber(2);
        doc2.setEmployee(e);
        doc2.setVaccineAdministrationDate(LocalDate.of(2021,1,1));
        ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<>();
        vaccineCardDocuments.add(doc);
        vaccineCardDocuments.add(doc2);
        VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
        vaccineDocumentList.setDocuments(vaccineCardDocuments);
        Boolean aasEnabled = true;
        VaxProofAutomaticApprovalResponse vaxResponse = new VaxProofAutomaticApprovalResponse();
        vaxResponse.setConfidenceScore(80);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", doc);
        params.put("documentList", vaccineDocumentList);
        params.put("aasEnabled",aasEnabled);
        params.put("aasProofsEndpoint","http://www.redhat.com");
        params.put("vaxProofAutomaticApprovalRequest", new VaxProofAutomaticApprovalRequest());
        params.put("automaticApprovalResponse", vaxResponse);
        ProcessInstance processInstance = ksession.startProcess("vax_card_aas_review_workflow",params);
        Long processInstanceId = processInstance.getId();
        PseudoClockScheduler sessionClock = ksession.getSessionClock();
        // Timer is set to 10 minutes, so advancing with 11.
        sessionClock.advanceTime(11, TimeUnit.MINUTES);
        assertProcessVarExists(processInstance,"documentList" );
        assertNodeTriggered(processInstanceId,"SET VARIABLES","NOTIFY EMPLOYEE - UNDER REVIEW","Auto Approver Timeout","APPROVAL THRESHOLD","NEXT STEP","SET FULLY VACCINATED FLAG","NOTIFY EMPLOYEE - ACCEPTED");
        assertProcessInstanceCompleted(processInstanceId);


    }

    @Test
    public void autoApprovalServiceSignalThresholdMetFullyVaccinated()
    {
        VaccineCardDocument doc = new VaccineCardDocument();
        Employee e = new Employee();
        doc.setVaccineBrand(VaccineBrand.PFIZER);
        doc.setVaccineAdministrationDate(LocalDate.of(2020,1,1));
        doc.setVaccineShotNumber(1);
        doc.setEmployee(e);
        doc.setProofType(ProofType.CDC);
        VaccineCardDocument doc2 = new VaccineCardDocument();
        doc2.setProofType(ProofType.CDC);
        doc2.setVaccineBrand(VaccineBrand.PFIZER);
        doc2.setVaccineShotNumber(2);
        doc2.setEmployee(e);
        doc2.setVaccineAdministrationDate(LocalDate.of(2021,1,1));
        ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<>();
        vaccineCardDocuments.add(doc);
        vaccineCardDocuments.add(doc2);
        VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
        vaccineDocumentList.setDocuments(vaccineCardDocuments);
        Boolean aasEnabled = true;
        VaxProofAutomaticApprovalResponse vaxResponse = new VaxProofAutomaticApprovalResponse();
        vaxResponse.setConfidenceScore(80);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", doc);
        params.put("documentList", vaccineDocumentList);
        params.put("aasEnabled",aasEnabled);
        params.put("aasProofsEndpoint","http://www.redhat.com");
        params.put("vaxProofAutomaticApprovalRequest", new VaxProofAutomaticApprovalRequest());
        params.put("automaticApprovalResponse", vaxResponse);

        ProcessInstance processInstance = ksession.startProcess("vax_card_aas_review_workflow",params);
        Long processInstanceId = processInstance.getId();
        ksession.signalEvent("EESignal", vaxResponse);

        assertNodeTriggered(processInstanceId,"SET VARIABLES","NOTIFY EMPLOYEE - UNDER REVIEW","EESignal","APPROVAL THRESHOLD","NEXT STEP","SET FULLY VACCINATED FLAG","NOTIFY EMPLOYEE - ACCEPTED");
        assertProcessInstanceNotActive(processInstanceId,ksession);
        assertProcessInstanceCompleted(processInstanceId);
    }

    @Ignore
    public void autoApprovalServiceThresholdMetIssuesDetected()
    {
        VaccineCardDocument doc = new VaccineCardDocument();
        VaccineCardDocument doc1 = new VaccineCardDocument();
        Employee e = new Employee();
        doc.setVaccineBrand(VaccineBrand.PFIZER);
        doc.setVaccineAdministrationDate(LocalDate.of(2022,3,3));
        doc.setVaccineShotNumber(0);
        doc.setEmployee(e);
        doc.setProofType(ProofType.CDC);
        doc1.setVaccineBrand(VaccineBrand.PFIZER);
        doc1.setVaccineAdministrationDate(LocalDate.of(2022,3,3));
        doc1.setVaccineShotNumber(0);
        doc1.setEmployee(e);
        doc1.setProofType(ProofType.CDC);
        ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<>();
        vaccineCardDocuments.add(doc);
        vaccineCardDocuments.add(doc1);
        VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
        vaccineDocumentList.setDocuments(vaccineCardDocuments);
        Boolean aasEnabled = true;
        VaxProofAutomaticApprovalResponse vaxResponse = new VaxProofAutomaticApprovalResponse();
        vaxResponse.setConfidenceScore(80);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", doc);
        params.put("documentList", vaccineDocumentList);
        params.put("aasEnabled",aasEnabled);
        params.put("aasProofsEndpoint","http://www.redhat.com");
        params.put("vaxProofAutomaticApprovalRequest", new VaxProofAutomaticApprovalRequest());
        params.put("automaticApprovalResponse", vaxResponse);
        ProcessInstance processInstance = ksession.startProcess("vax_card_aas_review_workflow",params);
        Long processInstanceId = processInstance.getId();
        assertProcessInstanceActive(processInstanceId, ksession);
        PseudoClockScheduler sessionClock = ksession.getSessionClock();
        // Timer is set to 10 minutes, so advancing with 12.
        sessionClock.advanceTime(12, TimeUnit.MINUTES);

        assertNodeTriggered(processInstanceId,"SET VARIABLES","NOTIFY EMPLOYEE - UNDER REVIEW","Auto Approver Timeout","APPROVAL THRESHOLD","NEXT STEP","NOTIFY HR","NOTIFY EMPLOYEE ISSUES WITH RECORDS");
        assertProcessInstanceNotActive(processInstanceId,ksession);
        assertProcessInstanceCompleted(processInstanceId);
    }

    @Test
    public void autoApprovalServiceThresholdMetDefault()
    {
        VaccineCardDocument doc = new VaccineCardDocument();
        Employee e = new Employee();
        doc.setVaccineBrand(VaccineBrand.PFIZER);
        doc.setVaccineAdministrationDate(LocalDate.now());
        doc.setVaccineShotNumber(1);
        doc.setEmployee(e);
        doc.setProofType(ProofType.CDC);
        VaccineCardDocument doc2 = new VaccineCardDocument();
        doc2.setVaccineBrand(VaccineBrand.PFIZER);
        doc2.setVaccineAdministrationDate(LocalDate.now());
        doc2.setVaccineShotNumber(2);
        doc2.setEmployee(e);
        doc2.setProofType(ProofType.CDC);
        ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<>();
        vaccineCardDocuments.add(doc);
        vaccineCardDocuments.add(doc2);
        VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
        vaccineDocumentList.setDocuments(vaccineCardDocuments);
        Boolean aasEnabled = true;
        VaxProofAutomaticApprovalResponse vaxResponse = new VaxProofAutomaticApprovalResponse();
        vaxResponse.setConfidenceScore(80);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", doc);
        params.put("documentList", vaccineDocumentList);
        params.put("aasEnabled",aasEnabled);
        params.put("aasProofsEndpoint","http://www.redhat.com");
        params.put("vaxProofAutomaticApprovalRequest", new VaxProofAutomaticApprovalRequest());
        params.put("automaticApprovalResponse", vaxResponse);
        ProcessInstance processInstance = ksession.startProcess("vax_card_aas_review_workflow",params);
        Long processInstanceId = processInstance.getId();
        PseudoClockScheduler sessionClock = ksession.getSessionClock();
        sessionClock.advanceTime(12, TimeUnit.MINUTES);
        
       // assert
        assertNodeTriggered(processInstanceId,"SET VARIABLES","NOTIFY EMPLOYEE - UNDER REVIEW","Auto Approver Timeout","APPROVAL THRESHOLD","NEXT STEP","SCHEDULE JOB TO SET FULLY VAX FLAG","NOTIFY EMPLOYEE - ACCEPTED");
        assertProcessInstanceNotActive(processInstanceId,ksession);
        assertProcessInstanceCompleted(processInstanceId);
    }


    @Test
    public void thresholdNotMetHrReview(){

        VaccineCardDocument doc = new VaccineCardDocument();
        Employee e = new Employee();
        e.setEmployeeId("mary");
        e.setAgencyCode("HR");
        doc.setVaccineBrand(VaccineBrand.PFIZER);
        doc.setVaccineAdministrationDate(LocalDate.of(2020,1,1));
        doc.setVaccineShotNumber(1);
        doc.setEmployee(e);
        doc.setProofType(ProofType.CDC);
        VaccineCardDocument doc2 = new VaccineCardDocument();
        doc2.setVaccineBrand(VaccineBrand.PFIZER);
        doc2.setVaccineAdministrationDate(LocalDate.of(2020,1,1));
        doc2.setVaccineShotNumber(2);
        doc2.setEmployee(e);
        doc2.setProofType(ProofType.CDC);
        ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<>();
        vaccineCardDocuments.add(doc);
        vaccineCardDocuments.add(doc2);
        VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
        vaccineDocumentList.setDocuments(vaccineCardDocuments);
        Boolean aasEnabled = true;
        VaxProofAutomaticApprovalResponse vaxResponse = new VaxProofAutomaticApprovalResponse();
        vaxResponse.setConfidenceScore(8);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", doc);
        params.put("documentList", vaccineDocumentList);
        params.put("aasEnabled",aasEnabled);
        params.put("aasProofsEndpoint","http://www.redhat.com");
        params.put("vaxProofAutomaticApprovalRequest", new VaxProofAutomaticApprovalRequest());
        params.put("automaticApprovalResponse", vaxResponse);
        params.put("employee",e);
        ProcessInstance processInstance = ksession.startProcess("vax_card_aas_review_workflow",params);
        Long processInstanceId = processInstance.getId();
        PseudoClockScheduler sessionClock = ksession.getSessionClock();
        // Timer is set to 60 seconds, so advancing with 70.
        sessionClock.advanceTime(12, TimeUnit.MINUTES);
        DocumentReview documentReview = new DocumentReview();
        documentReview.setReviewOutcome(DocumentReviewOutcome.DECLINED);
        List<Long> items = taskService.getTasksByProcessInstanceId(processInstanceId);
        System.out.println("process"+processInstance.getId());
        Task task = taskService.getTaskById(items.get(0));
        System.out.println("mary starting task " + task.getPeopleAssignments().getPotentialOwners() + task.getPeopleAssignments().getBusinessAdministrators());
        taskService.start(task.getId(), "Administrator");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("documentReview", documentReview);
        taskService.complete(task.getId(), "Administrator", results);
        assertNodeTriggered(processInstanceId,"SET VARIABLES","NOTIFY EMPLOYEE - UNDER REVIEW","Auto Approver Timeout","APPROVAL THRESHOLD","REVIEW VACCINATION CARD");
        assertProcessInstanceNotActive(processInstanceId,ksession);
        assertProcessInstanceCompleted(processInstanceId);

    }


    @Test
    public void autoApprovalServiceHumanTask()
    {
        VaccineCardDocument doc = new VaccineCardDocument();
        Employee e = new Employee();
        e.setId(123L);
        e.setEmployeeId("krisv");
        e.setFirstName("krisv");
        e.setLastName("last name");
        doc.setVaccineBrand(VaccineBrand.PFIZER);
        doc.setId(8352L);
        doc.setVaccineAdministrationDate(LocalDate.of(2020,1,1));
        doc.setVaccineShotNumber(1);
        doc.setEmployee(e);
        doc.setProofType(ProofType.CDC);
        VaccineCardDocument doc1 = new VaccineCardDocument();
        doc1.setSubmittedBy("abc");
        doc1.setProofType(ProofType.SMARTHEALTH);
        doc1.setVaccineBrand(VaccineBrand.PFIZER);
        doc1.setVaccineShotNumber(4);
        doc1.setEmployee(e);
        doc1.setId(8352L);
        doc1.setVaccineAdministrationDate(LocalDate.of(2020,1,1));
        doc1.setProofType(ProofType.CDC);
        ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<>();
        vaccineCardDocuments.add(doc);
        vaccineCardDocuments.add(doc1);
        VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
        vaccineDocumentList.setDocuments(vaccineCardDocuments);
        Boolean aasEnabled = true;
        VaxProofAutomaticApprovalResponse vaxResponse = new VaxProofAutomaticApprovalResponse();
        vaxResponse.setConfidenceScore(5);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("document", doc);
        params.put("documentList", vaccineDocumentList);
        params.put("aasEnabled",aasEnabled);
        params.put("aasProofsEndpoint","http://www.redhat.com");
        params.put("vaxProofAutomaticApprovalRequest", new VaxProofAutomaticApprovalRequest());
        params.put("automaticApprovalResponse", vaxResponse);

        DocumentReview documentReview = new DocumentReview();
        documentReview.setReviewOutcome(DocumentReviewOutcome.DECLINED);

        ProcessInstance processInstance = ksession.startProcess("vax_card_aas_review_workflow",params);
        Long processInstanceId = processInstance.getId();


        ksession.signalEvent("EESignal", vaxResponse);

        List<Long> items = taskService.getTasksByProcessInstanceId(processInstanceId);
        Task task = taskService.getTaskById(items.get(0));
        System.out.println("mary starting task " + task.getPeopleAssignments().getPotentialOwners() + task.getPeopleAssignments().getBusinessAdministrators());
        taskService.start(task.getId(), "Administrator");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("documentReview", documentReview);
        taskService.complete(task.getId(), "Administrator", results);

        assertNodeTriggered(processInstanceId,"SET VARIABLES","NOTIFY EMPLOYEE - UNDER REVIEW","EESignal","APPROVAL THRESHOLD","NOTIFY EMPLOYEE - DECLINED");
        assertProcessInstanceNotActive(processInstanceId,ksession);
        assertProcessInstanceCompleted(processInstanceId);

    }


}




