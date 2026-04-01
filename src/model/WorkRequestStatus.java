package model;

public enum WorkRequestStatus {
    draft,
    submitted,
    outboundApproved,
    inboundApproved,
    feasibilityReview,
    feasibilityComplete,
    approved,
    rejected,
    closed
}
