package com.redhat.vcs.model;

import java.io.Serializable;
import java.util.Objects;

public class Vaccination implements Serializable {

    private static final long serialVersionUID = 2561944963939020362L;

    String vaccineType;
    String inoculationDate;
    String lotNumber;

    public String getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(String vaccineType) {
        this.vaccineType = vaccineType;
    }

    public String getInoculationDate() {
        return inoculationDate;
    }

    public void setInoculationDate(String inoculationDate) {
        this.inoculationDate = inoculationDate;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    @Override
    public String toString() {
        return "Vaccination{" +
                "vaccineType='" + vaccineType + '\'' +
                ", inoculationDate='" + inoculationDate + '\'' +
                ", lotNumber='" + lotNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Vaccination other = (Vaccination) o;

        return Objects.equals(getVaccineType(), other.getVaccineType())
                && Objects.equals(getInoculationDate(), other.getInoculationDate())
                && Objects.equals(getLotNumber(), other.getLotNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(vaccineType, inoculationDate, lotNumber);
    }
}
