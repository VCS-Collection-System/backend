package com.redhat.vcs_kjar;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.redhat.vcs.model.VaccineBrand;
import com.redhat.vcs.model.VaccineCardDocument;
import com.redhat.vcs.model.VaccineDocumentList;

public class KjarUtils {
    private KjarUtils() {
        // Empty constructor to prevent instantiation
    }

    public static boolean nonBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
    public static boolean partialVaccineSubmission(VaccineDocumentList vaccineDocumentList) {
        List<VaccineCardDocument> documents = vaccineDocumentList.getDocuments();
        if (documents == null  || documents.size() == 0) {
            throw new IllegalArgumentException("documents is null");
        }
        // using first administration date and total number of first vaccine brand shots 
        LocalDate firstVaccineAdministrationDate = Collections.min(documents.parallelStream().map(document1 -> document1.getVaccineAdministrationDate()).collect(Collectors.toList()));
        VaccineBrand brand = documents.parallelStream().filter(document -> document.getVaccineAdministrationDate() == firstVaccineAdministrationDate).findFirst().get().getVaccineBrand();
        long maxShotNumber = documents.parallelStream().filter(document -> document.getVaccineBrand() == brand).count();
        return partialVaccineSubmission(brand, (int)maxShotNumber);
    }

    public static boolean partialVaccineSubmission(VaccineBrand brand, Integer shotNumber) {
        boolean multiShotVaccine =
            brand == VaccineBrand.ASTRAZENECA
            || brand == VaccineBrand.COMIRNATY
            || brand == VaccineBrand.COVAXIN
            || brand == VaccineBrand.COVISHIELD
            || brand == VaccineBrand.COVOVAX
            || brand == VaccineBrand.MODERNA
            || brand == VaccineBrand.NOVAVAX
            || brand == VaccineBrand.OXFORD
            || brand == VaccineBrand.PFIZER
            || brand == VaccineBrand.SINOPHARM
            || brand == VaccineBrand.SINOVAC_CORONAVAC
            || brand == VaccineBrand.SPIKEVAX
            || brand == VaccineBrand.SPUTNIK
            || brand == VaccineBrand.VAXZEVRIA;

        return multiShotVaccine && shotNumber == 1;
    }

    public static long numberOfDaysElapsed(LocalDate date) {
        return ChronoUnit.DAYS.between(date, today());
    }

    public static LocalDate today() {
        return LocalDate.now();
    }
}
