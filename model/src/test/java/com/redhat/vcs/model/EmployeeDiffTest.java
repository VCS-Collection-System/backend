package com.redhat.vcs.model;

import static com.redhat.vcs.model.EmployeeUtil.createEmployee;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.Test;

public class EmployeeDiffTest {

    @Test
    public void noLdapModificationTest() {

        boolean isHR = false;
        Employee e1 = createEmployee(isHR);
        Employee e2 = createEmployee(isHR);

        // Fields ignored when checking for modification
        e2.setDateOfBirth(LocalDate.of(1970, 1, 1));
        e2.setAlternateEmail("alternateEmail");
        // e2.setStatus(EmployeeStatus.INACTIVE);
        // e2.setLastUpdatedTime(LocalDateTime.now());

        assertFalse(new EmployeeDiff(e1, e1).ldapFieldChanged());
        assertFalse(new EmployeeDiff(e1, e2).ldapFieldChanged());
    }

    @Test
    public void agencyModificationTest() {
        Employee e1 = createEmployee(false);

        Employee e2 = createEmployee(false);
        e2.setAgencyName(UUID.randomUUID().toString());

        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        assertTrue(diff.ldapFieldChanged());
        assertTrue(diff.isAgencyNameChanged());
    }

    @Test
    public void divisionModificationTest() {
        Employee e1 = createEmployee(false);

        Employee e2 = createEmployee(false);
        e2.setDivisionName(UUID.randomUUID().toString());

        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        assertTrue(diff.ldapFieldChanged());
        assertTrue(diff.isDivisionNameChanged());
    }

    @Test
    public void emailModificationTest() {
        Employee e1 = createEmployee(false);

        Employee e2 = createEmployee(false);
        e2.setEmail(UUID.randomUUID().toString());

        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        assertTrue(diff.ldapFieldChanged());
        assertTrue(diff.isEmailChanged());
    }

    @Test
    public void firstNameModificationTest() {
        Employee e1 = createEmployee(false);

        Employee e2 = createEmployee(false);
        e2.setFirstName(UUID.randomUUID().toString());

        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        assertTrue(diff.ldapFieldChanged());
        assertTrue(diff.isFirstNameChanged());
    }

    // @Test
    // public void fullTimePartTimeModificationTest() {
    //     Employee e1 = createEmployee(false);

    //     Employee e2 = createEmployee(false);
    //     e2.setFullTimePartTime(UUID.randomUUID().toString());

    //     EmployeeDiff diff = new EmployeeDiff(e1, e2);
    //     assertTrue(diff.ldapFieldChanged());
    //     assertTrue(diff.isFullTimePartTimeChanged());
    // }

    @Test
    public void isHRModificationTest() {
        Employee e1 = createEmployee(false);
        Employee e2 = createEmployee(true);

        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        assertTrue(diff.ldapFieldChanged());
        assertTrue(diff.isHrChanged());
    }

    @Test
    public void lastNameModificationTest() {
        Employee e1 = createEmployee(false);

        Employee e2 = createEmployee(false);
        e2.setLastName(UUID.randomUUID().toString());

        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        assertTrue(diff.ldapFieldChanged());
        assertTrue(diff.isLastNameChanged());
    }

    // @Test
    // public void middleNameModificationTest() {
    //     Employee e1 = createEmployee(false);

    //     Employee e2 = createEmployee(false);
    //     e2.setMiddleName(UUID.randomUUID().toString());

    //     EmployeeDiff diff = new EmployeeDiff(e1, e2);
    //     assertTrue(diff.ldapFieldChanged());
    //     assertTrue(diff.isMiddleNameChanged());
    // }

    // @Test
    // public void ncidModificationTest() {
    //     Employee e1 = createEmployee(false);

    //     Employee e2 = createEmployee(false);
    //     e2.setNcid(UUID.randomUUID().toString());

    //     EmployeeDiff diff = new EmployeeDiff(e1, e2);
    //     assertTrue(diff.ldapFieldChanged());
    //     assertTrue(diff.isNcidChanged());
    // }

    @Test
    public void supervisorModificationTest() {
        Employee e1 = createEmployee(false);

        Employee e2 = createEmployee(false);
        e2.setSupervisor(UUID.randomUUID().toString());

        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        assertTrue(diff.ldapFieldChanged());
        assertTrue(diff.isSupervisorChanged());
    }

    // @Test
    // public void usersernameModificationTest() {
    //     Employee e1 = createEmployee(false);

    //     Employee e2 = createEmployee(false);
    //     e2.setUsername(UUID.randomUUID().toString());

    //     EmployeeDiff diff = new EmployeeDiff(e1, e2);
    //     assertTrue(diff.ldapFieldChanged());
    //     assertTrue(diff.isUsernameChanged());
    // }

    // @Test
    // public void userTypeModificationTest() {
    //     Employee e1 = createEmployee(false);

    //     Employee e2 = createEmployee(false);
    //     e2.setUserType(UUID.randomUUID().toString());

    //     EmployeeDiff diff = new EmployeeDiff(e1, e2);
    //     assertTrue(diff.ldapFieldChanged());
    //     assertTrue(diff.isUserTypeChanged());
    // }

    @Test
    public void agencyCodeModificationTest() {
        Employee e1 = createEmployee(false);

        Employee e2 = createEmployee(false);
        e2.setAgencyCode(UUID.randomUUID().toString());

        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        assertTrue(diff.ldapFieldChanged());
        assertTrue(diff.isAgencyCodeChanged());
    }

    @Test
    public void divisionCodeModificationTest() {
        Employee e1 = createEmployee(false);

        Employee e2 = createEmployee(false);
        e2.setDivisionCode(UUID.randomUUID().toString());

        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        assertTrue(diff.ldapFieldChanged());
        assertTrue(diff.isDivisionCodeChanged());
    }

    @Test
    public void test_genDiff_allFieldsDiff() {
        // Set up data
        Employee e1 = createEmployee(false);
        Employee e2 = createEmployee(false);

        e1.setHR(Boolean.FALSE);
        e1.setAgencyCode(UUID.randomUUID().toString());
        e1.setAgencyName(UUID.randomUUID().toString());
        e1.setDivisionCode(UUID.randomUUID().toString());
        e1.setDivisionName(UUID.randomUUID().toString());
        e1.setEmail(UUID.randomUUID().toString());
        e1.setFirstName(UUID.randomUUID().toString());
        e1.setLastName(UUID.randomUUID().toString());
        e1.setSupervisor(UUID.randomUUID().toString());

        e2.setHR(Boolean.TRUE);
        e2.setAgencyCode(UUID.randomUUID().toString());
        e2.setAgencyName(UUID.randomUUID().toString());
        e2.setDivisionCode(UUID.randomUUID().toString());
        e2.setDivisionName(UUID.randomUUID().toString());
        e2.setEmail(UUID.randomUUID().toString());
        e2.setFirstName(UUID.randomUUID().toString());
        e2.setLastName(UUID.randomUUID().toString());
        e2.setSupervisor(UUID.randomUUID().toString());

        final String EXPECTED_DIFF_STRING = "agencyCode['" + e1.getAgencyCode() + "', '" + e2.getAgencyCode() + "'], "
                + "agencyName['" + e1.getAgencyName() + "', '" + e2.getAgencyName() + "'], "
                + "divisionCode['" + e1.getDivisionCode() + "', '" + e2.getDivisionCode() + "'], "
                + "divisionName['" + e1.getDivisionName() + "', '" + e2.getDivisionName() + "'], "
                + "email['" + e1.getEmail() + "', '" + e2.getEmail() + "'], "
                + "isHr['" + e1.isHR() + "', '" + e2.isHR() + "'], "
                + "firstName['" + e1.getFirstName() + "', '" + e2.getFirstName() + "'], "
                + "lastName['" + e1.getLastName() + "', '" + e2.getLastName() + "'], "
                + "supervisor['" + e1.getSupervisor() + "', '" + e2.getSupervisor() + "'], ";

        // Set up mocks

        // Run test
        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        String diffString = diff.genDiff();

        // Validate
        assertEquals(
                EXPECTED_DIFF_STRING,
                diffString,
                "Diff string generated incorrectly");
    }

    @Test
    public void test_genDiff_allFieldsDiff_blankFirst() {
        // Set up data
        Employee e1 = createEmployee(false);
        Employee e2 = createEmployee(false);

        e1.setHR(Boolean.FALSE);
        e1.setAgencyCode("");
        e1.setAgencyName("");
        e1.setDivisionCode("");
        e1.setDivisionName("");
        e1.setEmail("");
        e1.setFirstName("");
        e1.setLastName("");
        e1.setSupervisor("");

        e2.setHR(Boolean.TRUE);
        e2.setAgencyCode(UUID.randomUUID().toString());
        e2.setAgencyName(UUID.randomUUID().toString());
        e2.setDivisionCode(UUID.randomUUID().toString());
        e2.setDivisionName(UUID.randomUUID().toString());
        e2.setEmail(UUID.randomUUID().toString());
        e2.setFirstName(UUID.randomUUID().toString());
        e2.setLastName(UUID.randomUUID().toString());
        e2.setSupervisor(UUID.randomUUID().toString());

        final String EXPECTED_DIFF_STRING = "agencyCode['" + e1.getAgencyCode() + "', '" + e2.getAgencyCode() + "'], "
                + "agencyName['" + e1.getAgencyName() + "', '" + e2.getAgencyName() + "'], "
                + "divisionCode['" + e1.getDivisionCode() + "', '" + e2.getDivisionCode() + "'], "
                + "divisionName['" + e1.getDivisionName() + "', '" + e2.getDivisionName() + "'], "
                + "email['" + e1.getEmail() + "', '" + e2.getEmail() + "'], "
                + "isHr['" + e1.isHR() + "', '" + e2.isHR() + "'], "
                + "firstName['" + e1.getFirstName() + "', '" + e2.getFirstName() + "'], "
                + "lastName['" + e1.getLastName() + "', '" + e2.getLastName() + "'], "
                + "supervisor['" + e1.getSupervisor() + "', '" + e2.getSupervisor() + "'], ";

        // Set up mocks

        // Run test
        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        String diffString = diff.genDiff();

        // Validate
        assertEquals(
                EXPECTED_DIFF_STRING,
                diffString,
                "Diff string generated incorrectly");
    }

    @Test
    public void test_genDiff_allFieldsDiff_blankSecond() {
        // Set up data
        Employee e1 = createEmployee(false);
        Employee e2 = createEmployee(false);

        e1.setHR(Boolean.FALSE);
        e1.setAgencyCode(UUID.randomUUID().toString());
        e1.setAgencyName(UUID.randomUUID().toString());
        e1.setDivisionCode(UUID.randomUUID().toString());
        e1.setDivisionName(UUID.randomUUID().toString());
        e1.setEmail(UUID.randomUUID().toString());
        e1.setFirstName(UUID.randomUUID().toString());
        e1.setLastName(UUID.randomUUID().toString());
        e1.setSupervisor(UUID.randomUUID().toString());

        e2.setHR(Boolean.TRUE);
        e2.setAgencyCode("");
        e2.setAgencyName("");
        e2.setDivisionCode("");
        e2.setDivisionName("");
        e2.setEmail("");
        e2.setFirstName("");
        e2.setLastName("");
        e2.setSupervisor("");

        final String EXPECTED_DIFF_STRING = "agencyCode['" + e1.getAgencyCode() + "', '" + e2.getAgencyCode() + "'], "
                + "agencyName['" + e1.getAgencyName() + "', '" + e2.getAgencyName() + "'], "
                + "divisionCode['" + e1.getDivisionCode() + "', '" + e2.getDivisionCode() + "'], "
                + "divisionName['" + e1.getDivisionName() + "', '" + e2.getDivisionName() + "'], "
                + "email['" + e1.getEmail() + "', '" + e2.getEmail() + "'], "
                + "isHr['" + e1.isHR() + "', '" + e2.isHR() + "'], "
                + "firstName['" + e1.getFirstName() + "', '" + e2.getFirstName() + "'], "
                + "lastName['" + e1.getLastName() + "', '" + e2.getLastName() + "'], "
                + "supervisor['" + e1.getSupervisor() + "', '" + e2.getSupervisor() + "'], ";

        // Set up mocks

        // Run test
        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        String diffString = diff.genDiff();

        // Validate
        assertEquals(
                EXPECTED_DIFF_STRING,
                diffString,
                "Diff string generated incorrectly");
    }

    @Test
    public void test_genDiff_noFieldsDiff() {
        // Set up data
        Employee e1 = createEmployee(false);
        Employee e2 = e1;

        e2.setHR(true);
        e2.setAgencyCode(UUID.randomUUID().toString());
        e2.setAgencyName(UUID.randomUUID().toString());
        e2.setDivisionCode(UUID.randomUUID().toString());
        e2.setDivisionName(UUID.randomUUID().toString());
        e2.setEmail(UUID.randomUUID().toString());
        e2.setFirstName(UUID.randomUUID().toString());
        e2.setLastName(UUID.randomUUID().toString());
        e2.setSupervisor(UUID.randomUUID().toString());

        final String EXPECTED_DIFF_STRING = "";

        // Set up mocks

        // Run test
        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        String diffString = diff.genDiff();

        // Validate
        assertEquals(
                EXPECTED_DIFF_STRING,
                diffString,
                "Diff string generated incorrectly");
    }

    @Test
    public void test_genDiff_noFieldsDiff_blank() {
        // Set up data
        Employee e1 = createEmployee(false);
        Employee e2 = e1;

        e2.setHR(true);

        final String EXPECTED_DIFF_STRING = "";

        // Set up mocks

        // Run test
        EmployeeDiff diff = new EmployeeDiff(e1, e2);
        String diffString = diff.genDiff();

        // Validate
        assertEquals(
                EXPECTED_DIFF_STRING,
                diffString,
                "Diff string generated incorrectly");
    }
}
