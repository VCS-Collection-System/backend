package com.redhat.vcs.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class DocumentReview implements Serializable {

    static final long serialVersionUID = 1L;

    // Not making this a FK for now in order to allow review by HR employee not in the system
    // @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    // @JoinColumn(name = "reviewer_employee_id", referencedColumnName = "employee_id")
    @Column(name = "reviewer_employee_id")
    private String reviewerEmployeeId;

    @Column(name = "reviewer_notes")
    private String reviewerNotes;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_outcome")
    private DocumentReviewOutcome reviewOutcome;

    @Column(name = "review_reject_reason")
    private String rejectReason;

    public String getReviewerEmployeeId() {
        return reviewerEmployeeId;
    }

    public void setReviewerEmployeeId(String id) {
        this.reviewerEmployeeId = id;
    }

    public String getReviewerNotes() {
        return reviewerNotes;
    }

    public void setReviewerNotes(String reviewerNotes) {
        this.reviewerNotes = reviewerNotes;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public DocumentReviewOutcome getReviewOutcome() {
        return reviewOutcome;
    }

    public void setReviewOutcome(DocumentReviewOutcome reviewOutcome) {
        this.reviewOutcome = reviewOutcome;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    @Override
    public String toString() {
        return "DocumentReview ["
                + "reviewerEmployeeId=" + Utils.maskedString()
                + ", reviewerNotes=" + getReviewerNotes()
                + ", reviewDate=" + getReviewDate()
                + ", reviewOutcome=" + getReviewOutcome()
                + ", rejectReason=" + getRejectReason()
                + "]";
    }

}
