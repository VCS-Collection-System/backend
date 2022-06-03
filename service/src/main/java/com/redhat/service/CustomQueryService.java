package com.redhat.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.redhat.vcs.model.CountryConfigurations;
import com.redhat.vcs.model.CovidTestResultDocument;
import com.redhat.vcs.model.DocumentReviewOutcome;
import com.redhat.vcs.model.DocumentTaskMapping;
import com.redhat.vcs.model.Employee;
import com.redhat.vcs.model.PositiveTestResult;
import com.redhat.vcs.model.VaccineCardDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CustomQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomQueryService.class);

    private static final String AGENCY_NAME_KEY = "agencyName";
    private static final String DOCUMENT_ID_KEY = "documentId";
    private static final String EMPLOYEE_ID_KEY = "employeeId";
    private static final String STATUS_KEY = "status";
    private static final String AGENCY_KEY = "agency";

    @PersistenceContext
    EntityManager em;

    public DocumentTaskMapping getDocumentTaskMapping(Long documentId) {
        return em
            .createNamedQuery("document_task_mapping_by_document_id", DocumentTaskMapping.class)
            .setParameter(DOCUMENT_ID_KEY, documentId)
            .getSingleResult();
    }

    public DocumentTaskMapping getDocumentTaskMapping(Long documentId, String agencyName) {
        return em
            .createNamedQuery("document_task_mapping_by_document_id_and_agency_name", DocumentTaskMapping.class)
            .setParameter(DOCUMENT_ID_KEY, documentId)
            .setParameter(AGENCY_NAME_KEY, agencyName)
            .getSingleResult();
    }

    public Employee getEmployee(String employeeId) {
        return em
                .createNamedQuery("employee_by_employee_id", Employee.class)
                .setParameter(EMPLOYEE_ID_KEY, employeeId)
                .getSingleResult();
    }

    public Employee getEmployee(String employeeId, String agencyName) {
        return em
            .createNamedQuery("employee_by_employee_id_and_agency_name", Employee.class)
            .setParameter(EMPLOYEE_ID_KEY, employeeId)
            .setParameter(AGENCY_NAME_KEY, agencyName)
            .getSingleResult();
    }

    /**
     *
     * @param employee
     * @return the employee that will be in the db after call, or null if couldn't talk to db.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Employee persistOrOverwriteEmployee(Employee employee) {
        if (employee.getEmployeeId() == null || employee.getEmployeeId().trim().isEmpty()) {
            throw new RuntimeException("Employee id cannot be empty");
        }

        Employee output;

        try {
            output = em.find(Employee.class, employee.getEmployeeId());

            if (output == null) {
                em.persist(employee);
                output = employee;
            } else if (!output.equals(employee)) {
                // Changing logic thusly:
                // pull record down and determine if field is null/empty or populated
                // for data coming in, if it is not null or empty, overrwrite field.
                String employeeId = output.getEmployeeId();

                output.setAgencyCode(getEmployeeValue(output.getAgencyCode(), employee.getAgencyCode(), employeeId,
                        "agencyCode"));
                output.setAgencyName(getEmployeeValue(output.getAgencyName(), employee.getAgencyName(), employeeId,
                        AGENCY_NAME_KEY));
                output.setAlternateEmail(getEmployeeValue(output.getAlternateEmail(), employee.getAlternateEmail(), employeeId,
                        "alternateEmail"));
                output.setDateOfBirth(getEmployeeValue(output.getDateOfBirth(), employee.getDateOfBirth(), employeeId,
                        "dateOfBirth"));
                output.setDivisionCode(getEmployeeValue(output.getDivisionCode(), employee.getDivisionCode(), employeeId,
                        "divisionCode"));
                output.setDivisionName(getEmployeeValue(output.getDivisionName(), employee.getDivisionName(), employeeId,
                        "divisionName"));
                output.setEmail(getEmployeeValue(output.getEmail(), employee.getEmail(), employeeId,
                        "email"));
                output.setFirstName(getEmployeeValue(output.getFirstName(), employee.getFirstName(), employeeId,
                        "firstName"));
                output.setLastName(getEmployeeValue(output.getLastName(), employee.getLastName(), employeeId,
                        "lastName"));
                output.setSupervisor(getEmployeeValue(output.getSupervisor(), employee.getSupervisor(), employeeId,
                        "supervisor"));
                output.setHR(getEmployeeValue(output.isHR(), employee.isHR(), employeeId,
                        "isHR"));
                output = em.merge(output);
            }
        } catch (Exception e) {
            LOG.error("Error persisting/saving " + employee, e);
            throw e;
        }
        return output;
    }

    private boolean isEmpty(Object o) {
        return (null == o || "".equals(o.toString()));
    }

    private <T> T getEmployeeValue(T dbValue, T uiValue, String employeeId, String fieldName) {
        T output = dbValue;

        if (isEmpty(uiValue)) {
            return output;
        }
        if (!isEmpty(uiValue) && isEmpty(dbValue)) {
            output = uiValue;
        }
        if (!isEmpty(uiValue) && !isEmpty(dbValue)) {
            output = uiValue;
            if (LOG.isTraceEnabled())
                LOG.trace("overwriting " + fieldName + " with data: " + dbValue + " with UI data: " + uiValue
                + " for employee " + employeeId);
        }
        return output;
    }

    public List<CovidTestResultDocument> getCovidTestResult(String employeeId) {
        return em
                .createNamedQuery("covid_test_result_doc_by_employee_id", CovidTestResultDocument.class)
                .setParameter(EMPLOYEE_ID_KEY, employeeId)
                .getResultList();
    }

    public List<CovidTestResultDocument> getCovidTestResult(String employeeId, String agencyName) {
        return em
            .createNamedQuery("covid_test_result_doc_by_employee_id_and_agency_name", CovidTestResultDocument.class)
            .setParameter(EMPLOYEE_ID_KEY, employeeId)
            .setParameter(AGENCY_NAME_KEY, agencyName)
            .getResultList();
    }

    public List<VaccineCardDocument> getVaccineDocument(String employeeId) {
        return em
                .createNamedQuery("vax_doc_by_employee_id", VaccineCardDocument.class)
                .setParameter(EMPLOYEE_ID_KEY, employeeId)
                .getResultList();
    }

    public List<VaccineCardDocument> getVaccineDocument(String employeeId, String agencyName) {
        return em
            .createNamedQuery("vax_doc_by_employee_id_and_agency_name", VaccineCardDocument.class)
            .setParameter(EMPLOYEE_ID_KEY, employeeId)
            .setParameter(AGENCY_NAME_KEY, agencyName)
            .getResultList();
    }

    public List<VaccineCardDocument> getVaccineDocument(
            String employeeId,
            DocumentReviewOutcome status) {

        return em
                .createNamedQuery("vax_doc_by_employee_id_and_status", VaccineCardDocument.class)
                .setParameter(EMPLOYEE_ID_KEY, employeeId)
                .setParameter(STATUS_KEY, status)
                .getResultList();
    }

    public List<VaccineCardDocument> getVaccineDocument(
            String employeeId,
            DocumentReviewOutcome status,
            String agencyName) {
        return em
            .createNamedQuery("vax_doc_by_employee_id_and_status_and_agency_name", VaccineCardDocument.class)
            .setParameter(EMPLOYEE_ID_KEY, employeeId)
            .setParameter(STATUS_KEY, status)
            .setParameter(AGENCY_NAME_KEY, agencyName)
            .getResultList();
    }

    public List<PositiveTestResult> getPositiveTestResult() {
        return em
                .createNamedQuery("covid_positive", PositiveTestResult.class)
                .getResultList();
    }

    public List<PositiveTestResult> getPositiveTestResultByAgency(String agency) {
        return em
            .createNamedQuery("covid_positive_by_agency", PositiveTestResult.class)
            .setParameter(AGENCY_KEY, agency)
            .getResultList();
    }

    public List<PositiveTestResult> getPositiveTestResultByAgencyName(String agencyName) {
        return em
            .createNamedQuery("covid_positive_by_agency_name", PositiveTestResult.class)
            .setParameter(AGENCY_KEY, agencyName)
            .getResultList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CountryConfigurations persistCountryConfigurations(CountryConfigurations countryConfig) {

        if (countryConfig.getAgencyName() == null || countryConfig.getAgencyName().trim().isEmpty()) {
            throw new RuntimeException("agency name cannot be empty");
        }

        CountryConfigurations output;

        try {
            output = em.find(CountryConfigurations.class, countryConfig.getAgencyName().toLowerCase());
            if(output == null)
            {
                countryConfig.setAgencyName(countryConfig.getAgencyName().toLowerCase());
                em.persist(countryConfig);
                output = countryConfig;
            }
            else if (!output.equals(countryConfig)) {
                countryConfig.setAgencyName(countryConfig.getAgencyName().toLowerCase());
                output.setAllowPdf(countryConfig.getAllowPdf());
                output.setCdc(countryConfig.getCdc());
                output.setConsentCheckbox(countryConfig.getConsentCheckbox());
                output.setConsentCheckboxAlternate(countryConfig.getConsentCheckboxAlternate());
                output.setCovidTest(countryConfig.getCovidTest());
                output.setCustomInput(countryConfig.getCustomInput());
                output.setDateOfBirth(countryConfig.getDateOfBirth());
                output.setDivoc(countryConfig.getDivoc());
                output.setEugc(countryConfig.getEugc());
                output.setGlobalConfigurationEnabled(countryConfig.getGlobalConfigurationEnabled());
                output.setGreenPass(countryConfig.getGreenPass());
                output.setHrDashboard(countryConfig.getHrDashboard());
                output.setHrSearch(countryConfig.getHrSearch());
                output.setProof(countryConfig.getProof());
                output.setProofOptional(countryConfig.getProofOptional());
                output.setRecovery(countryConfig.getRecovery());
                output = em.merge(output);

            }
        }
        catch (Exception e) {
            LOG.error("Error persisting/saving " + countryConfig, e);
            throw e;
        }
        return output;
    }


    public CountryConfigurations getCountryConfigurations(String agencyName) {
        return em.find(CountryConfigurations.class, agencyName);
    }



    @Transactional
    public void deleteCovidPositiveTestResult(long id) {
        delete(PositiveTestResult.class, id);
    }

    @Transactional
    public void deleteVaccineRecordById(long id ){

        em
            .createNamedQuery("remove_vax_doc_by_id")
            .setParameter("id", id)
            .executeUpdate();
    }

    @Transactional
    private <T> void delete(Class<T> t, long id) {
        try {
            T p = em.find(t, id);

            if (p != null) {
                em.remove(p);
            }
        } catch(Exception e) {
            LOG.error("Error deleting {} {}", t, id);
            throw new RuntimeException(e);
        }
    }
}
