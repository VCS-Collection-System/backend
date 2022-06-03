package com.redhat.vcs_kjar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import com.redhat.vcs.model.CovidTestResultDocument;
import com.redhat.vcs.model.Document;
import com.redhat.vcs.model.Employee;
import com.redhat.vcs.model.VaccineCardDocument;
import com.redhat.vcs.model.VaccineDocumentList;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import jdk.internal.joptsimple.internal.Strings;

/**
 * This class contains the unit (logic / flow) tests for NotificationUtils.
 *
 * For strict unit testing, the following will be mocked:
 *     - All external interactions.
 *     - Test data, unless creating the mock is less trivial than creating the actual object (e.g., Strings).
 *
 * Note: Due to the (unenforced) contract between equals and hashCode, both methods will be tested together.
 */
public class NotificationUtilsTest {

    @Test
    public void test_getEmployeeFullName_success() {
        // Set up data
        final String LAST_NAME = "Bond";
        final String FIRST_NAME = "James Bond";

        Employee employee = new Employee();

        employee.setFirstName(FIRST_NAME);
        employee.setLastName(LAST_NAME);

        // Set up mocks

        // Run test
        String fullName = NotificationUtils.getEmployeeFullName(employee);

        // Validate
        assertEquals(
                LAST_NAME + ", " + FIRST_NAME,
                fullName,
                "Employee full name did not parse correctly");
    }

    @Test
    public void test_getEmployeeEmails_success_noEmail() {
        // Set up data
        final String EMAIL = null;
        final String ALT_EMAIL = null;

        Employee employee = new Employee();

        employee.setEmail(EMAIL);
        employee.setAlternateEmail(ALT_EMAIL);

        // Set up mocks

        // Run test
        String emails = NotificationUtils.getEmployeeEmails(employee);

        // Validate
        assertEquals(
                Strings.EMPTY,
                emails,
                "Employee emails should be empty");
    }

    @Test
    public void test_getEmployeeEmails_success_noAltEmail() {
        // Set up data
        final String EMAIL = "oldperson@aol.com";
        final String ALT_EMAIL = null;

        Employee employee = new Employee();

        employee.setEmail(EMAIL);
        employee.setAlternateEmail(ALT_EMAIL);

        // Set up mocks

        // Run test
        String emails = NotificationUtils.getEmployeeEmails(employee);

        // Validate
        assertEquals(
                EMAIL,
                emails,
                "Employee emails did not parse correctly");
    }

    @Test
    public void test_getEmployeeEmails_success_noPrimaryEmail() {
        // Set up data
        final String EMAIL = null;
        final String ALT_EMAIL = "tom@myspace.com";

        Employee employee = new Employee();

        employee.setEmail(EMAIL);
        employee.setAlternateEmail(ALT_EMAIL);

        // Set up mocks

        // Run test
        String emails = NotificationUtils.getEmployeeEmails(employee);

        // Validate
        assertEquals(
                ALT_EMAIL,
                emails,
                "Employee emails did not parse correctly");
    }

    @Test
    public void test_getEmployeeEmails_success_twoEmails() {
        // Set up data
        final String EMAIL = "oldperson@aol.com";
        final String ALT_EMAIL = "tom@myspace.com";

        Employee employee = new Employee();

        employee.setEmail(EMAIL);
        employee.setAlternateEmail(ALT_EMAIL);

        // Set up mocks

        // Run test
        String emails = NotificationUtils.getEmployeeEmails(employee);

        // Validate
        assertEquals(
                EMAIL + "," + ALT_EMAIL,
                emails,
                "Employee emails did not parse correctly");
    }

    /**
     * TODO: getHrMailList tests rely on a system property, that can't as-is be easily overridden between tests.
     */
    @Disabled
    @Test
    public void test_getHrMailingList_success_byDoc_prod() {
    }

    /**
     * TODO: getHrMailList tests rely on a system property, that can't as-is be easily overridden between tests.
     */
    @Disabled
    @Test
    public void test_getHrMailingList_success_byDoc_uat() {
        // Set up data

        // Set up mocks

        // Run test

        // Validate

    }

    /**
     * TODO: getHrMailList tests rely on a system property, that can't as-is be easily overridden between tests.
     */
    @Disabled
    @Test
    public void test_getHrMailingList_success_byDoc_dev() {
        // Set up data

        // Set up mocks

        // Run test

        // Validate

    }

    @Test
    public void test_getHrMailingList_fail_byDoc_noDoc() {
        // Set up data
        Document document = null;

        // Set up mocks

        // Run test & Validate (one step due to Exception)
        assertThrows(RuntimeException.class, () -> NotificationUtils.getHrMailingList(document));
    }

    @Test
    public void test_getHrMailingList_fail_byDoc_noEmployee() {
        // Set up data
        Document document = new CovidTestResultDocument();
        document.setEmployee(null);

        // Set up mocks

        // Run test & Validate (one step due to Exception)
        assertThrows(RuntimeException.class, () -> NotificationUtils.getHrMailingList(document));
    }

    @Test
    public void test_daysAfter_success() {
        // Set up data
        final LocalDate DATE = LocalDate.of(1999, 12, 31);
        final Long DAYS_TO_ADD = 1L;

        // Set up mocks

        // Run test
        Date date = NotificationUtils.daysAfter(DATE, DAYS_TO_ADD);

        // Validate
        assertEquals(
                Date.from(DATE.plusDays(DAYS_TO_ADD).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                date,
                "Date did not calculate correctly");
    }

    @Test
    public void test_daysAfterTestDate_success() {
        // Set up data
        final LocalDate DATE = LocalDate.of(1999, 12, 31);
        final Long DAYS_TO_ADD = 1L;

        CovidTestResultDocument document = new CovidTestResultDocument();
        document.setCovidTestDate(DATE);

        // Set up mocks

        // Run test
        Date date = NotificationUtils.daysAfterTestDate(document, DAYS_TO_ADD);

        // Validate
        assertEquals(
                Date.from(DATE.plusDays(DAYS_TO_ADD).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                date,
                "Date did not calculate correctly");
    }

    /**
     * TODO: Test relies on value of 'now', which over time will cause data drift. Need a way to override!
     */
    @Disabled
    @Test
    public void test_fullyVaccinatedDateOrNow_success_date_now() {
        // Set up data

        // Set up mocks

        // Run test

        // Validate

    }

    @Test
    public void test_fullyVaccinatedDateOrNow_success_date_future() {
        // Set up data
        final LocalDate VACCINE_ADMIN_DATE = LocalDate.of(2999, 12, 1);

        // Set up mocks

        // Run test
        Date date = NotificationUtils.fullyVaccinatedDateOrNow(VACCINE_ADMIN_DATE);

        // Validate
        assertEquals(
                Date.from(VACCINE_ADMIN_DATE.plusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                date,
                "Date did not calculate correctly");
    }

    /**
     * TODO: Test relies on value of 'now', which over time will cause data drift. Need a way to override!
     */
    @Disabled
    @Test
    public void test_fullyVaccinatedDateOrNow_success_document_now() {
        // Set up data

        // Set up mocks

        // Run test

        // Validate

    }

    @Test
    public void test_fullyVaccinatedDateOrNow_success_document_future() {
        // Set up data
        final LocalDate VACCINE_ADMIN_DATE = LocalDate.of(2999, 12, 1);

        VaccineCardDocument doc = new VaccineCardDocument();
        doc.setVaccineAdministrationDate(VACCINE_ADMIN_DATE);
        ArrayList<VaccineCardDocument> vaccineCardDocuments = new ArrayList<>();
        vaccineCardDocuments.add(doc);
        VaccineDocumentList vaccineDocumentList = new VaccineDocumentList();
        vaccineDocumentList.setDocuments(vaccineCardDocuments);

        // Set up mocks

        // Run test
        Date date = NotificationUtils.fullyVaccinatedDateOrNow(vaccineDocumentList);

        // Validate
        assertEquals(
                Date.from(VACCINE_ADMIN_DATE.plusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                date,
                "Date did not calculate correctly");
    }
}
