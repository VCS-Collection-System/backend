package com.redhat.vcs_kjar;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.redhat.vcs.model.CovidTestResultDocument;
import com.redhat.vcs.model.Document;
import com.redhat.vcs.model.Employee;
import com.redhat.vcs.model.Utils;
import com.redhat.vcs.model.VaccineCardDocument;
import com.redhat.vcs.model.VaccineDocumentList;

public class NotificationUtils {
    private NotificationUtils() {
        // Empty constructor to prevent instantiation
    }

    public static String getEmployeeFullName(Employee employee) {
        return employee.getLastName() + ", " + employee.getFirstName();
    }

    public static String getEmployeeEmails(Employee employee) {
        Set<String> s = new HashSet<>();

        if (!Utils.isBlank(employee.getEmail())) {
            s.add(employee.getEmail());
        }

        if (!Utils.isBlank(employee.getAlternateEmail())) {
            s.add(employee.getAlternateEmail());
        }

        return s.stream().collect(Collectors.joining(","));
    }

    public static String getHrMailingList(Document document) {
        if (document == null) {
            throw new RuntimeException("Document is null");
        }

        if (document.getEmployee() == null) {
            throw new RuntimeException("Document.getEmployee() is null");
        }

        return getHrMailingList(document.getEmployee().getAgencyName());
    }

    public static String getHrMailingList(String agencyName) {
        String mailingList;

        if (NotificationUtilsConfig.PROD_ENV_NAME.equalsIgnoreCase(NotificationUtilsConfig.ENVIRONMENT)) {
            mailingList = NotificationUtilsConfig.getHrMailingListMap().get(normalize(agencyName));
        } else if (NotificationUtilsConfig.UAT_ENV_NAME.equalsIgnoreCase(NotificationUtilsConfig.ENVIRONMENT)) {
            mailingList = NotificationUtilsConfig.getHrMailingListUatMap().get(normalize(agencyName));
        } else {
            mailingList = NotificationUtilsConfig.getHrMailingListDevMap().get(normalize(agencyName));
        }

        return mailingList;
    }

    public static Date daysAfter(LocalDate date, long daysToAdd) {
        return Date.from(date.plusDays(daysToAdd).atStartOfDay(NotificationUtilsConfig.DEFAULT_ZONE_ID).toInstant());
    }

    public static Date daysAfterTestDate(CovidTestResultDocument document, long daysToAdd) {
        return daysAfter(document.getCovidTestDate(), daysToAdd);
    }

    public static Date fullyVaccinatedDateOrNow(VaccineDocumentList vaccineDocumentList) {
        List<VaccineCardDocument> documents = vaccineDocumentList.getDocuments();
        if (documents == null  || documents.size() == 0) {
            throw new IllegalArgumentException("documents is null");
        }
        
        LocalDate administrationDate = Collections.max(documents.parallelStream().map(document1 -> document1.getVaccineAdministrationDate()).collect(Collectors.toList()));
        
        return fullyVaccinatedDateOrNow(administrationDate);
    }

    public static Date fullyVaccinatedDateOrNow(LocalDate administrationDate) {
        Date fullyVaxDate = daysAfter(administrationDate, 14);
        Date now = new Date();
        return fullyVaxDate.compareTo(now) <= 0 ? now : fullyVaxDate;
    }

    private static String normalize(String s) {
        return s!= null ? s.toUpperCase() : null;
    }
}
