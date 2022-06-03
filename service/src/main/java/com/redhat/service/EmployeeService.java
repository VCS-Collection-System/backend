package com.redhat.service;

import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.redhat.vcs.model.Employee;
import com.redhat.vcs.model.EmployeeDiff;
import com.redhat.vcs.model.EmployeeLog;
import com.redhat.vcs.model.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component

public class EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeService.class);

    @PersistenceContext
    EntityManager em;

    /**
     * Update employee non LDAP fields
     *
     * @param employeeIn
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Employee update(Employee employeeIn) {
        String employeeId = employeeIn.getEmployeeId();
        String alternateEmail   = employeeIn.getAlternateEmail();
        LocalDate dob = employeeIn.getDateOfBirth();

        Employee employee = null;

        try {
            TypedQuery<Employee> tq = em.createQuery("FROM Employee WHERE employeeId = :employeeId", Employee.class);
            employee = tq.setParameter("employeeId", employeeId)
                    .getSingleResult();
        } catch (Exception e ) {
            LOG.info("Failed to find employee in Database - will be added.");
        }

        if (employee == null) {
            LOG.debug("Employee not in database - adding.");
            em.persist(employeeIn);
            employee = employeeIn;
        }

        boolean touched = false;

        if (dob != null && !dob.equals(employee.getDateOfBirth())) {
            LOG.debug(">>> Updating employee DOB ");
            employee.setDateOfBirth(dob);
            touched = true;
        }

        if (employeeIn.getVaxConsentDate() != null && !employeeIn.getVaxConsentDate().equals(employee.getVaxConsentDate())) {
            LOG.debug(">>> Updating employee vax consent date ");
            employee.setVaxConsentDate(employeeIn.getVaxConsentDate());
            touched = true;
        }

        if (alternateEmail != null && !alternateEmail.trim().isEmpty()) {
            if (!Utils.isEmailValid(alternateEmail)) {
                throw new RuntimeException("Invalid email address '" + alternateEmail+"'");
            }

            if (!alternateEmail.equals(employee.getAlternateEmail())) {
                LOG.debug(">>> Updating employee alternate email");
                employee.setAlternateEmail(alternateEmail);
                touched = true;
            }
        }

        if (touched) {
            try {
                employee = em.merge(employee);
                
            } catch (Exception e) {
                LOG.error("Failed to update employee with database id {}", employee.getId(), e);
            }
        }

        return employee;
    }

    /**
     * Syncs employee retrieved from LDAP with the data base
     *
     * @param ldapEmployee
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sync(Employee ldapEmployee) {
        if (ldapEmployee == null) {
            throw new RuntimeException("Employee is null");
        }

        if (ldapEmployee.getEmployeeId() == null || ldapEmployee.getEmployeeId().trim().isEmpty()) {
            throw new RuntimeException("Employee id cannot be empty");
        }

        try {
            Employee dbEmployee = em.find(Employee.class, ldapEmployee.getId());
            if (dbEmployee == null) {
                em.persist(ldapEmployee);
                LOG.debug("New employee. {}", ldapEmployee);
            } else {
                EmployeeDiff diff = new EmployeeDiff(dbEmployee, ldapEmployee);
                if (diff.ldapFieldChanged()) {
                    String changes = diff.genDiff();
                    LOG.debug("LDAP field(s) modified. Employee id: {} \n>>> {}", dbEmployee.getId(), changes);

                    // Backup (employee log) and update LDAP fields
                    EmployeeLog employeeLog = dbEmployee.asEmployeeLog();
                    employeeLog.setChanges(changes);
                    em.persist(employeeLog);

                    dbEmployee.overwriteLdapFields(ldapEmployee);
                } else {
                    LOG.debug("No LDAP modifications. Employee id: {}", dbEmployee.getId());
                }

                em.merge(dbEmployee);

            }
        } catch (Exception e) {
            LOG.error("Error while syncing employee", e);
            throw e;
        }
    }

}
