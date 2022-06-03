package com.redhat.vcs.model;

public class EmployeeDiff {

    private final boolean agencyCodeChanged;
    private final boolean agencyNameChanged;
    private final boolean divisionCodeChanged;
    private final boolean divisionNameChanged;
    private final boolean emailChanged;
    private final boolean firstNameChanged;
    private final boolean isHrChanged;
    private final boolean lastNameChanged;
    private final boolean supervisorChanged;

    private Employee e1;
    private Employee e2;

    public EmployeeDiff(Employee e1, Employee e2) {
        this.e1 = e1;
        this.e2 = e2;

        agencyCodeChanged   = ! same(e1.getAgencyCode(), e2.getAgencyCode());
        agencyNameChanged   = ! same(e1.getAgencyName(), e2.getAgencyName());
        divisionCodeChanged = ! same(e1.getDivisionCode(), e2.getDivisionCode());
        divisionNameChanged = ! same(e1.getDivisionName(), e2.getDivisionName());
        emailChanged        = ! same(e1.getEmail(), e2.getEmail());
        firstNameChanged    = ! same(e1.getFirstName(), e2.getFirstName());
        isHrChanged         = ! same(e1.isHR(), e2.isHR());
        lastNameChanged     = ! same(e1.getLastName(), e2.getLastName());
        supervisorChanged   = ! same(e1.getSupervisor(), e2.getSupervisor());
    }

    public boolean ldapFieldChanged() {
        return agencyCodeChanged
            || agencyNameChanged
            || divisionCodeChanged
            || divisionNameChanged
            || emailChanged
            || isHrChanged
            || firstNameChanged
            || lastNameChanged
            || supervisorChanged;
    }

    public boolean isAgencyCodeChanged() {
        return agencyCodeChanged;
    }

    public boolean isAgencyNameChanged() {
        return agencyNameChanged;
    }

    public boolean isDivisionCodeChanged() {
        return divisionCodeChanged;
    }

    public boolean isDivisionNameChanged() {
        return divisionNameChanged;
    }

    public boolean isEmailChanged() {
        return emailChanged;
    }

    public boolean isFirstNameChanged() {
        return firstNameChanged;
    }

    public boolean isHrChanged() {
        return isHrChanged;
    }

    public boolean isLastNameChanged() {
        return lastNameChanged;
    }

    public boolean isSupervisorChanged() {
        return supervisorChanged;
    }

    private boolean same(String s1, String s2) {
        return (isBlank(s1) && isBlank(s2)) || (s1 != null && s1.equals(s2));
    }

    private boolean same(boolean b1, boolean b2) {
        return b1 == b2;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public String genDiff() {
        StringBuilder sb = new StringBuilder();

        if (agencyCodeChanged) {
            sb
                    .append("agencyCode['")
                    .append(e1.getAgencyCode())
                    .append("', '")
                    .append(e2.getAgencyCode())
                    .append("'], ");
        }

        if (agencyNameChanged) {
            sb
                    .append("agencyName['")
                    .append(e1.getAgencyName())
                    .append("', '")
                    .append(e2.getAgencyName())
                    .append("'], ");
        }

        if (divisionCodeChanged) {
            sb
                    .append("divisionCode['")
                    .append(e1.getDivisionCode())
                    .append("', '")
                    .append(e2.getDivisionCode())
                    .append("'], ");
        }

        if (divisionNameChanged) {
            sb
                    .append("divisionName['")
                    .append(e1.getDivisionName())
                    .append("', '")
                    .append(e2.getDivisionName())
                    .append("'], ");
        }

        if (emailChanged) {
            sb
                    .append("email['")
                    .append(e1.getEmail())
                    .append("', '")
                    .append(e2.getEmail())
                    .append("'], ");
        }

        if (isHrChanged) {
            sb.append("isHr['")
                    .append(e1.isHR())
                    .append("', '")
                    .append(e2.isHR())
                    .append("'], ");
        }

        if (firstNameChanged) {
            sb.append("firstName['")
                    .append(e1.getFirstName())
                    .append("', '")
                    .append(e2.getFirstName())
                    .append("'], ");
        }

        if (lastNameChanged) {
            sb
                    .append("lastName['")
                    .append(e1.getLastName())
                    .append("', '")
                    .append(e2.getLastName())
                    .append("'], ");
        }

        if (supervisorChanged) {
            sb.append("supervisor['")
                    .append(e1.getSupervisor())
                    .append("', '")
                    .append(e2.getSupervisor())
                    .append("'], ");
        }

        return sb.toString();
    }
}
