package com.redhat.vcs.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class VaxProofAutomaticApprovalRequest implements Serializable {

    private static final long serialVersionUID = -1593926205229589834L;

    private String correlationId;
    private String firstName;
    private String lastName;
    private String dob;
    private List<Proof> proofs;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public List<Proof> getProofs() {
        return proofs;
    }

    public void setProofs(List<Proof> proofs) {
        this.proofs = proofs;
    }

    @Override
    public String toString() {
        return "VaxProofAutomaticApprovalRequest{"
                + "correlationId='" + getCorrelationId() + '\''
                + ", firstName='" + Utils.maskedString() + '\''
                + ", lastName='" + Utils.maskedString() + '\''
                + ", dob='" + Utils.maskedDOB() + '\''
                + ", proofs=" + getProofs()
                + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        VaxProofAutomaticApprovalRequest other = (VaxProofAutomaticApprovalRequest) obj;

        return Objects.equals(getCorrelationId(), other.getCorrelationId())
                && Objects.equals(getFirstName(), other.getFirstName())
                && Objects.equals(getLastName(), other.getLastName())
                && Objects.equals(getDob(), other.getDob())
                && Objects.equals(getProofs(), other.getProofs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCorrelationId(), getFirstName(), getLastName(), getDob(), getProofs());
    }
}
