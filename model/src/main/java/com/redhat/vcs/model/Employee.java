package com.redhat.vcs.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.persistence.Index;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;

@Entity
@Table(
        name = "cs_employee",
        indexes = { @Index(name = "idx_cs_employee_employee_email", columnList = "email"),
            @Index(name = "idx_cs_employee_employee_agency_code", columnList = "agency_code"),
            @Index(name = "idx_cs_employee_employee_agency_name", columnList = "agency_name"),
            @Index(name = "idx_cs_employee_employee_division_code", columnList = "division_code") })

@NamedQuery(
        name = "employee_by_employee_id",
        query = "SELECT d FROM Employee d WHERE d.employeeId=:employeeId"
)

@NamedQuery(
        name = "employee_by_employee_id_and_agency_name",
        query = "SELECT d FROM Employee d WHERE d.employeeId=:employeeId and lower(d.agencyName)=lower(:agencyName)")
    
public class Employee implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "EMPLOYEE_ID_GENERATOR", strategy = javax.persistence.GenerationType.AUTO)
    @SequenceGenerator(name = "EMPLOYEE_ID_GENERATOR", sequenceName = "EMPLOYEE_ID_SEQ", allocationSize = 1)
    protected Long id;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;

    @Column(name = "agency_code")
    private String agencyCode;

    @Column(name = "agency_name")
    private String agencyName;

    @Column(name = "division_code")
    private String divisionCode;

    @Column(name = "division_name")
    private String divisionName;

    private String supervisor;

    @Column(name = "is_hr")
    private Boolean isHR;

    // Fields added in the UI (not in LDAP)
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "vax_consent_date")
    private LocalDateTime vaxConsentDate;

    @Column(name = "alternate_email")
    private String alternateEmail;

    @Column(name = "alt_first_name")
    private String altFirstName;

    @Column(name = "alt_last_name")
    private String altLastName;

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (id != null && Utils.isBlank(employeeId)){
            employeeId = id.toString();
            id = null;
        }

        if (Utils.isBlank(employeeId)) {
            errors.add("Employee id is missing.");
        }
        if (Utils.isBlank(agencyCode)) {
            errors.add("Employee agency is missing.");
        }
        if (!Utils.isEmailBlankOrValid(email)) {
            errors.add("Invalid email address '" + email + "'");
        }
        if (!Utils.isEmailBlankOrValid(alternateEmail)) {
            errors.add("Invalid alternate email address '" + alternateEmail + "'");
        }
        if (Utils.isBlank(email) && Utils.isBlank(alternateEmail)) {
            errors.add("Both email and alternate email are missing");
        }

        return errors;
    }

    /**
     * @return EmployeeLog from this instance
     */
    public EmployeeLog asEmployeeLog() {
        EmployeeLog tmp = new EmployeeLog();

        tmp.setAgencyCode(getAgencyCode());
        tmp.setAgencyName(getAgencyName());
        tmp.setDivisionCode(getDivisionCode());
        tmp.setDivisionName(getDivisionName());
        tmp.setEmail(getEmail());
        tmp.setFirstName(getFirstName());
        tmp.setHR(isHR());
        tmp.setLastName(getLastName());
        tmp.setSupervisor(getSupervisor());
        tmp.setEmployeeId(getEmployeeId());

        return tmp;
    }

    public void overwriteLdapFields(Employee e) {
        this.setAgencyCode(e.getAgencyCode());
        this.setAgencyName(e.getAgencyName());
        this.setDivisionCode(e.getDivisionCode());
        this.setDivisionName(e.getDivisionName());
        this.setEmail(e.getEmail());
        this.setFirstName(e.getFirstName());
        this.setHR(e.isHR());
        this.setLastName(e.getLastName());
        this.setSupervisor(e.getSupervisor());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getDivisionCode() {
        return divisionCode;
    }

    public void setDivisionCode(String divisionCode) {
        this.divisionCode = divisionCode;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public Boolean isHR() {
        return isHR;
    }

    public void setHR(Boolean isHR) {
        this.isHR = isHR;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDateTime getVaxConsentDate() {
        return vaxConsentDate;
    }

    public void setVaxConsentDate(LocalDateTime vaxConsentDate) {
        this.vaxConsentDate = vaxConsentDate;
    }

    public String getAlternateEmail() {
        return alternateEmail;
    }

    public void setAlternateEmail(String alternateEmail) {
        this.alternateEmail = alternateEmail;
    }

    public String getAltFirstName() {
        return altFirstName;
    }

    public void setAltFirstName(String altFirstName) {
        this.altFirstName = altFirstName;
    }

    public String getAltLastName() {
        return altLastName;
    }

    public void setAltLastName(String altLastName) {
        this.altLastName = altLastName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((agencyCode == null) ? 0 : agencyCode.hashCode());
        result = prime * result + ((agencyName == null) ? 0 : agencyName.hashCode());
        result = prime * result + ((altFirstName == null) ? 0 : altFirstName.hashCode());
        result = prime * result + ((altLastName == null) ? 0 : altLastName.hashCode());
        result = prime * result + ((alternateEmail == null) ? 0 : alternateEmail.hashCode());
        result = prime * result + ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
        result = prime * result + ((vaxConsentDate == null) ? 0 : vaxConsentDate.hashCode());
        result = prime * result + ((divisionCode == null) ? 0 : divisionCode.hashCode());
        result = prime * result + ((divisionName == null) ? 0 : divisionName.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((employeeId == null) ? 0 : employeeId.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((isHR == null) ? 0 : isHR.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((supervisor == null) ? 0 : supervisor.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Employee other = (Employee) obj;

        return Objects.equals(this.getAgencyCode(), other.getAgencyCode())
                && Objects.equals(this.getAgencyName(), other.getAgencyName())
                && Objects.equals(this.getAltFirstName(), other.getAltFirstName())
                && Objects.equals(this.getAltLastName(), other.getAltLastName())
                && Objects.equals(this.getAlternateEmail(), other.getAlternateEmail())
                && Objects.equals(this.getDateOfBirth(), other.getDateOfBirth())
                && Objects.equals(this.getVaxConsentDate(), other.getVaxConsentDate())
                && Objects.equals(this.getDivisionCode(), other.getDivisionCode())
                && Objects.equals(this.getDivisionName(), other.getDivisionName())
                && Objects.equals(this.getEmail(), other.getEmail())
                && Objects.equals(this.getEmployeeId(), other.getEmployeeId())
                && Objects.equals(this.getFirstName(), other.getFirstName())
                && Objects.equals(this.isHR(), other.isHR())
                && Objects.equals(this.getLastName(), other.getLastName())
                && Objects.equals(this.getSupervisor(), other.getSupervisor());
    }

    @Override
    public String toString() {
        return "Employee ["
                + "id=" + getId()
                + ", employeeId=" + Utils.maskedString()
                + ", firstName=" + Utils.maskedString()
                + ", lastName=" + Utils.maskedString()
                + ", email=" + Utils.maskedString()
                + ", agencyCode=" + getAgencyCode()
                + ", agencyName=" + getAgencyName()
                + ", divisionCode=" + getDivisionCode()
                + ", divisionName=" + getDivisionName()
                + ", supervisor=" + Utils.maskedString()
                + ", isHR=" + isHR()
                + ", dateOfBirth=" + Utils.maskedDOB()
                + ", vaxConsentDate=" + getVaxConsentDate()
                + ", alternateEmail=" + Utils.maskedString()
                + ", altFirstName=" + Utils.maskedString()
                + ", altLastName=" + Utils.maskedString()
                + "]";
    }
}
