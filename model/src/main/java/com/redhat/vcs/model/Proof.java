package com.redhat.vcs.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Proof implements Serializable {

    private static final long serialVersionUID = -2935508889295981901L;

    private String type;
    private List<Vaccination> vaccinations;
    private String image;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Vaccination> getVaccinations() {
        return vaccinations;
    }

    public void setVaccinations(List<Vaccination> vaccinations) {
        this.vaccinations = vaccinations;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Proof{"
                + "type='" + getType() + '\''
                + ", vaccinations=" + getVaccinations() + '\''
                + ", image=" + getImage()
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Proof proof = (Proof) o;

        return Objects.equals(getType(), proof.getType())
                && Objects.equals(getVaccinations(), proof.getVaccinations())
                && Objects.equals(getImage(), proof.getImage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getVaccinations(), getImage());
    }
}
