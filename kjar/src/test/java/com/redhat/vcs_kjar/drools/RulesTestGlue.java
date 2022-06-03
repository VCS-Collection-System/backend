package com.redhat.vcs_kjar.drools;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.redhat.vcs.model.DocumentReview;
import com.redhat.vcs.model.DocumentReviewOutcome;
import com.redhat.vcs.model.ProofType;
import com.redhat.vcs.model.VaccineBrand;
import com.redhat.vcs.model.VaccineCardDocument;
import com.redhat.vcs.model.VaccineDocumentList;
import com.redhat.vcs.model.VaxProofAutomaticApprovalResponse;
import com.redhat.vcs_kjar.AutomaticApproval;
import com.redhat.vcs_kjar.AutomaticApprovalThreshold;
import com.redhat.vcs_kjar.VaxBusinessRulesOutcome;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class RulesTestGlue {
    private VaccineCardDocument vaccineCardDocument1 = null;
    private VaccineCardDocument vaccineCardDocument2 = null;
    private VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
    private VaxBusinessRulesOutcome vaxBusinessRulesOutcome = null;
    private AutomaticApproval automaticApproval = null;
    private AutomaticApprovalThreshold automaticApprovalThreshold = null;
    private VaxProofAutomaticApprovalResponse vaxProofAutomaticApprovalResponse = null;
    private DocumentReview documentReview = null;
    private List<Object> facts = new ArrayList<Object>();

    @Before()
    public void clearFacts() {
        facts.clear();
    }

    @When("^there is a Vaccine Document$")
    public void initVaccineCardDocument()
    {
        vaccineCardDocument1 = new VaccineCardDocument();
        facts.add(vaccineCardDocument1);
    }

    @When("^there are Vaccine Documents$")
    public void initVaccineCardDocuments()
    {
        vaccineCardDocument1 = new VaccineCardDocument();
        ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<>();
        vaccineCardDocuments.add(vaccineCardDocument1);
        vaccineDocumentList.setDocuments(vaccineCardDocuments);
        facts.add(vaccineDocumentList.getDocuments());
    }
    @When("^there is a List of Vaccine Documents$")
    public void initVaccineCardDocumentList()
    {
        vaccineCardDocument1 = new VaccineCardDocument();
        vaccineCardDocument2 = new VaccineCardDocument();
        ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<>();
        vaccineCardDocuments.add(vaccineCardDocument1);
        vaccineCardDocuments.add(vaccineCardDocument2);
        vaccineDocumentList.setDocuments(vaccineCardDocuments);
        facts.add(vaccineDocumentList);
    }

    @When("^there is a Document Review Outcome$")
    public void initDocumentReviewOutcome() {        
        vaxBusinessRulesOutcome = new VaxBusinessRulesOutcome();
        facts.add(vaxBusinessRulesOutcome);
    }

    @When("^there is an Automatic Approval Inquiry$")
    public void initAutomaticApproval() {        
        automaticApproval = new AutomaticApproval();
        facts.add(automaticApproval);
    }

    @When("^there is a Vaccination Proof Automatic Approval Response$")
    public void initVaxAutoApprovalResponse() {        
        vaxProofAutomaticApprovalResponse = new VaxProofAutomaticApprovalResponse();
        facts.add(vaxProofAutomaticApprovalResponse);
    }

    @When("^there is an Automatic Approval Threshold$")
    public void initAutomaticApprovalThreshold() {        
        automaticApprovalThreshold = new AutomaticApprovalThreshold();
        facts.add(automaticApprovalThreshold);
    }

    @When("^there is a Document Review$")
    public void initDocumentReview() {        
        documentReview = new DocumentReview();

        // NOTE: The Default enum value is "ACCEPTED", so we explicitly set
        //       it to "INCONCLUSIVE" here
        documentReview.setReviewOutcome(DocumentReviewOutcome.INCONCLUSIVE);
        facts.add(documentReview);
    }    

    @When("^the vaccine brand is (.*)$")
    public void setVaccineBrand(String brand) {
        vaccineCardDocument1.setVaccineBrand(VaccineBrand.valueOf(brand));
    }

    @When("^the vaccine brand for two shot vaccine is (.*)$")
    public void setTwoShotVaccineBrand(String brand) {
        vaccineDocumentList.getDocuments().get(1).setVaccineBrand(VaccineBrand.valueOf(brand));
    }

    @When("^the vaccine administration date was (-?\\d+) days ago$")
    public void setAdminDate(int daysAgo) {
        LocalDate date = LocalDate.now().minusDays(daysAgo);
        vaccineCardDocument1.setVaccineAdministrationDate(date);
    }

    @When("^the vaccine administration date for second shot was (-?\\d+) days ago$")
    public void setSecondShotAdminDate(int daysAgo) {
        LocalDate date = LocalDate.now().minusDays(daysAgo);
        System.out.println("list has"+vaccineDocumentList.getDocuments());
        vaccineDocumentList.getDocuments().get(1).setVaccineAdministrationDate(date);
    }

    @When("^the shot number is (-?\\d+)$")
    public void setShotNumber(int shotNumber) {
        vaccineCardDocument1.setVaccineShotNumber(shotNumber);
    }
    @When("^the shot number for two shot vaccine is (-?\\d+)$")
    public void setTwoShotNumber(int shotNumber) {
        vaccineDocumentList.getDocuments().get(1).setVaccineShotNumber(shotNumber);
    }

    @When("^proof type is (.*)$")
    public void setProofType(String prooftype) {
        vaccineCardDocument1.setProofType(ProofType.valueOf(prooftype));
    }

    @When("^there is a confidence score of (-?\\d+)$")
    public void setConfidenceScore(int confidenceScore) {
        vaxProofAutomaticApprovalResponse.setConfidenceScore(confidenceScore);
    }    
    
    @Then("^vaccination status is (.*)$")
    public void validateVaccinationStatus(String stringStatus) {
        Boolean status = Boolean.parseBoolean(stringStatus);

        fireRules("vax-card-next-step");
        assertTrue(vaxBusinessRulesOutcome.getFullyVaccinated() == status);
    }

    @Then("^valid shot number is (.*)$")
    public void validateShotNumber(String stringStatus) {
        Boolean status = Boolean.parseBoolean(stringStatus);

        fireRules("vax-card-next-step");
        assertTrue( (vaxBusinessRulesOutcome.getIssueList().size() == 0) == status);
    }

    @Then("^qualified for automatic approval is (.*)$")
    public void validateAutoApprovalQualification(String stringStatus) {
        Boolean status = Boolean.parseBoolean(stringStatus);

        fireRules("vax-card-automatic-approval-applicable");
        assertTrue( automaticApproval.isApplicable() == status);
    }

    @Then("^threshold met is (.*)$")
    public void thresholdMet(String thresholdMetString) {
        fireRules("vax-card-approval-threshold");
        Boolean thresholdMet = Boolean.parseBoolean(thresholdMetString);
        assertTrue( automaticApprovalThreshold.isMet() == thresholdMet);
    }

    @Then("^auto approved is (.*)$")
    public void autoApproved(String autoApprovedString) {
        Boolean autoApproved = Boolean.parseBoolean(autoApprovedString);
        assertTrue( vaccineCardDocument1.getAutoApproved() == autoApproved);
    }

    @Then("^review outcome is (.*)$")
    public void reviewOutcome(String reviewOutcome) {
        assertTrue( documentReview.getReviewOutcome() == 
                    DocumentReviewOutcome.valueOf(reviewOutcome));
    }

    @Then("^vaccine document review added (.*)$")
    public void doucmentReviewSet(String reviewSetString) {
        Boolean reviewSet = Boolean.parseBoolean(reviewSetString);
        assertTrue( (vaccineCardDocument1.getReview() != null) == reviewSet);
    }

    private void fireRules(String ruleFlowGroup) {
        DroolsUtil.fireAllRules(facts, ruleFlowGroup);
    }
}
