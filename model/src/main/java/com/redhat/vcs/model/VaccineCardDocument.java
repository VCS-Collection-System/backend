package com.redhat.vcs.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;


@Entity
@Table(
    name = "cs_vaccine_document",
    indexes = {
        @Index(name = "idx_cs_vaccine_document_submitted_by", columnList = "submitted_by"),
        @Index(name = "idx_cs_vaccine_document_fully_vaccinated", columnList = "fully_vaccinated_flag"),
        @Index(name = "idx_cs_vaccine_document_brand", columnList = "brand"),
        @Index(name = "idx_cs_vaccine_document_administration_date", columnList = "administration_date"),
        @Index(name = "idx_cs_vaccine_document_shot_number", columnList = "shot_number")
    }
)
@NamedQuery(
    name = "vax_doc_by_employee_id",
    query = "SELECT d FROM VaccineCardDocument d WHERE d.employee.employeeId=:employeeId"
)
@NamedQuery(
    name = "vax_doc_by_employee_id_and_agency_name",
    query = "SELECT d FROM VaccineCardDocument d WHERE d.employee.employeeId=:employeeId and lower(d.employee.agencyName)=lower(:agencyName)"
)

@NamedQuery(
    name = "vax_doc_by_employee_id_and_status",
    query = "SELECT d FROM VaccineCardDocument d WHERE d.employee.employeeId=:employeeId and d.review.reviewOutcome=:status"
)
@NamedQuery(
    name = "vax_doc_by_employee_id_and_status_and_agency_name",
    query = "SELECT d FROM VaccineCardDocument d WHERE d.employee.employeeId=:employeeId and lower(d.employee.agencyName)=lower(:agencyName) and d.review.reviewOutcome=:status"
)

@NamedQuery(
    name = "latest_vax_doc_by_employee_id_and_shot_number",
    query = "SELECT d FROM VaccineCardDocument d WHERE d.employee.employeeId = :employeeId and d.vaccineShotNumber = :shotNumber order by d.submissionDate desc"
)
@NamedQuery(
    name = "remove_vax_doc_by_id",
    query = "DELETE FROM VaccineCardDocument d WHERE d.id = :id"
)

public class VaccineCardDocument extends Document implements Serializable {

    static final long serialVersionUID = 1L;

    @Column(name = "brand")
    @Enumerated(EnumType.STRING)
    private VaccineBrand vaccineBrand;

    @Column(name = "administration_date")
    private LocalDate vaccineAdministrationDate;

    @Column(name = "fully_vaccinated_flag")
    private Boolean fullVaccinatedFlag = false;

    @Column(name = "shot_number")
    private Integer vaccineShotNumber;

    @Column(name = "clinicname")
    private String clinicName;

    @Column(name = "lotnumber")
    private String lotNumber;

    @Column(name = "prooftype")
    private ProofType proofType;

    @Column(name = "confidence_score")
    private Integer confidenceScore;

    @ManyToOne()
    @JoinColumn(name = "document_list_id", insertable = false, updatable = false)
    private VaccineDocumentList documentList;

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();

        if (vaccineBrand == null) {
            errors.add("Vaccine brand is missing.");
        }

        if (vaccineAdministrationDate == null) {
            errors.add("Vaccine administration date is missing.");
        }

        if (vaccineShotNumber < 1 ) {
            errors.add("Invalid vaccine shot number '" + vaccineShotNumber +"'");
        }

        return errors;
    }

    public VaccineBrand getVaccineBrand() {
        return vaccineBrand;
    }

    public void setVaccineBrand(VaccineBrand vaccineBrand) {
        this.vaccineBrand = vaccineBrand;
    }

    public LocalDate getVaccineAdministrationDate() {
        return vaccineAdministrationDate;
    }

    public void setVaccineAdministrationDate(LocalDate vaccineAdministrationDate) {
        this.vaccineAdministrationDate = vaccineAdministrationDate;
    }

    public Integer getVaccineShotNumber() {
        return vaccineShotNumber;
    }

    public void setVaccineShotNumber(Integer vaccineShotNumber) {
        this.vaccineShotNumber = vaccineShotNumber;
    }

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Integer confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Boolean isFullVaccinatedFlag() {
        return fullVaccinatedFlag;
    }

    public void setFullVaccinatedFlag(Boolean fullVaccinatedFlag) {
        this.fullVaccinatedFlag = fullVaccinatedFlag;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public ProofType getProofType() {
        return proofType;
    }

    public void setProofType(ProofType proofType) {
        this.proofType = proofType;
    }

    @JsonBackReference
    public VaccineDocumentList getDocumentList() {
        return documentList;
    }

    public void setDocumentList(VaccineDocumentList documentList) {
        this.documentList = documentList;
    }

    @Override
    public String toString() {
        return "VaccineCardDocument ["
                + "vaccineAdministrationDate=" + getVaccineAdministrationDate()
                + ", vaccineBrand=" + getVaccineBrand()
                + ", vaccineShotNumber=" + getVaccineShotNumber()
                + " # " + super.toString()
                + "]";
    }
}
