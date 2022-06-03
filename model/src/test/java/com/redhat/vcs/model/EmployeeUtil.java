package com.redhat.vcs.model;

import java.util.UUID;

public class EmployeeUtil {

    public static Employee createEmployee(boolean isHR) {
        Employee e = new Employee();
        e.setEmployeeId(UUID.randomUUID().toString());
        e.setAgencyCode("agencyCode");
        e.setAgencyName("agencyName");
        e.setDivisionCode("divisionCode");
        e.setDivisionName("divisionName");
        e.setEmail("email@example.com");
        e.setFirstName("firstName");
        // e.setFullTimePartTime("fullTimePartTime");
        e.setHR(isHR);
        e.setLastName("lastName");
        // e.setMiddleName("middleName");
        // e.setNcid("ncid");
        e.setSupervisor("supervisor");
        return e;
    }

}
