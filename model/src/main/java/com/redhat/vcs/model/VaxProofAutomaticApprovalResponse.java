package com.redhat.vcs.model;

import java.util.Objects;

/**
 * non-entity, fact only model.
 *
 * @author mshin
 *
 */
public class VaxProofAutomaticApprovalResponse implements java.io.Serializable {

	static final long serialVersionUID = 1L;

    private String correlationId;

    private Integer confidenceScore;

    private Boolean hardFail;

    private String report;

    private ProofType proofType;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Integer confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Boolean getHardFail() {
        return hardFail;
    }

    public void setHardFail(Boolean hardFail) {
        this.hardFail = hardFail;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public ProofType getProofType() {
        return proofType;
    }

    public void setProofType(ProofType proofType) {
        this.proofType = proofType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((getConfidenceScore() == null) ? 0 : getConfidenceScore().hashCode());
        result = prime * result + ((getCorrelationId() == null) ? 0 : getCorrelationId().hashCode());
        result = prime * result + ((getHardFail() == null) ? 0 : getHardFail().hashCode());
        result = prime * result + ((getProofType() == null) ? 0 : getProofType().hashCode());
        result = prime * result + ((getReport() == null) ? 0 : getReport().hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        VaxProofAutomaticApprovalResponse other = (VaxProofAutomaticApprovalResponse) obj;

        return Objects.equals(this.getConfidenceScore(), other.getConfidenceScore())
                && Objects.equals(this.getCorrelationId(), other.getCorrelationId())
                && Objects.equals(this.getHardFail(), other.getHardFail())
                && Objects.equals(this.getProofType(), other.getProofType())
                && Objects.equals(this.getReport(), other.getReport());
    }

    @Override
    public String toString() {
        return "VaxProofAutomaticApprovalResponse ["
                + "correlationId=" + getCorrelationId()
                + ", confidenceScore=" + getConfidenceScore()
                + ", hardFail=" + getHardFail()
                + ", report=" + getReport()
                + ", proofType=" + getProofType()
                + "]";
    }
}
