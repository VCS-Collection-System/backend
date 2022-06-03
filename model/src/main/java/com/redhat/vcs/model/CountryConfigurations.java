package com.redhat.vcs.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(
    name = "cs_country_configurations"
)
public class CountryConfigurations implements Serializable{

    static final long serialVersionUID = 1L;

    @Id
    @Column(name = "agency_name")
    private String agencyName;

    @Column(name = "date_of_birth")
    private Boolean dateOfBirth;

    @Column(name = "recovery")
    private Boolean recovery;

    @Column(name = "consent_checkbox")
    private Boolean consentCheckbox;

    @Column(name = "consent_checkbox_alternate")
    private Boolean consentCheckboxAlternate;

    @Column(name = "allow_pdf")
    private Boolean allowPdf;

    @Column(name = "covid_test")
    private Boolean covidTest;

    @Column(name = "custom_input")
    private Boolean customInput;

    @Column(name = "hr_dashboard")
    private Boolean hrDashboard;

    @Column(name = "hr_search")
    private Boolean hrSearch;

    @Column(name = "proof_optional")
    private Boolean proofOptional;

    @Column(name = "proof")
    private Boolean proof;

    @Column(name = "global_configuration_enabled")
    private Boolean globalConfigurationEnabled;

    @Column(name ="cdc")
    private Boolean cdc;

    @Column(name="greenPass")
    private Boolean greenPass;

    @Column(name="divoc")
    private Boolean divoc;

    @Column(name="eugc")
    private Boolean eugc;

    public Boolean getEugc() {
        return eugc;
    }

    public void setEugc(Boolean eugc) {
        this.eugc = eugc;
    }

    public Boolean getCdc() {
        return cdc;
    }

    public void setCdc(Boolean cdc) {
        this.cdc = cdc;
    }

    public Boolean getGreenPass() {
        return greenPass;
    }

    public void setGreenPass(Boolean greenPass) {
        this.greenPass = greenPass;
    }

    public Boolean getDivoc() {
        return divoc;
    }

    public void setDivoc(Boolean divoc) {
        this.divoc = divoc;
    }

    public Boolean getGlobalConfigurationEnabled() {
        return globalConfigurationEnabled;
    }

    public void setGlobalConfigurationEnabled(Boolean globalConfigurationEnabled) {
        this.globalConfigurationEnabled = globalConfigurationEnabled;
    }

    public Boolean getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Boolean dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getConsentCheckbox() {
        return consentCheckbox;
    }

    public void setConsentCheckbox(Boolean consentCheckbox) {
        this.consentCheckbox = consentCheckbox;
    }

    public Boolean getConsentCheckboxAlternate() {
        return consentCheckboxAlternate;
    }

    public void setConsentCheckboxAlternate(Boolean consentCheckboxAlternate) {
        this.consentCheckboxAlternate = consentCheckboxAlternate;
    }

    public Boolean getAllowPdf() {
        return allowPdf;
    }

    public void setAllowPdf(Boolean allowPdf) {
        this.allowPdf = allowPdf;
    }

    public Boolean getCovidTest() {
        return covidTest;
    }

    public void setCovidTest(Boolean covidTest) {
        this.covidTest = covidTest;
    }

    public Boolean getCustomInput() {
        return customInput;
    }

    public void setCustomInput(Boolean customInput) {
        this.customInput = customInput;
    }

    public Boolean getHrDashboard() {
        return hrDashboard;
    }

    public void setHrDashboard(Boolean hrDashboard) {
        this.hrDashboard = hrDashboard;
    }

    public Boolean getHrSearch() {
        return hrSearch;
    }

    public void setHrSearch(Boolean hrSearch) {
        this.hrSearch = hrSearch;
    }

    public Boolean getProofOptional() {
        return proofOptional;
    }

    public void setProofOptional(Boolean proofOptional) {
        this.proofOptional = proofOptional;
    }

    public Boolean getProof() {
        return proof;
    }

    public void setProof(Boolean proof) {
        this.proof = proof;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public Boolean getRecovery() {
        return recovery;
    }

    public void setRecovery(Boolean recovery) {
        this.recovery = recovery;
    }


    @Override
    public String toString() {
        return "Country Configurations ["
                + "agencyName=" + getAgencyName()
                + ", recovery=" + getRecovery()
                + "]";
    }

    
}
