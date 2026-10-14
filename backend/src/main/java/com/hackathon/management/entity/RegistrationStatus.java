package com.hackathon.management.entity;

public enum RegistrationStatus {
    DRAFT,
    REGISTERED,
    APPROVED,
    IN_PROGRESS,
    SUBMITTED,
    COMPLETED,
    REJECTED;

    /**
     * Enforces the permitted state transition workflow.
     * Allowed:
     * DRAFT -> REGISTERED
     * REGISTERED -> APPROVED, REJECTED
     * APPROVED -> IN_PROGRESS
     * IN_PROGRESS -> SUBMITTED
     * SUBMITTED -> COMPLETED, REJECTED
     * REJECTED -> REGISTERED (resubmission)
     */
    public boolean canTransitionTo(RegistrationStatus target) {
        if (this == target) {
            return true;
        }
        return switch (this) {
            case DRAFT -> target == REGISTERED;
            case REGISTERED -> target == APPROVED || target == REJECTED;
            case APPROVED -> target == IN_PROGRESS;
            case IN_PROGRESS -> target == SUBMITTED;
            case SUBMITTED -> target == COMPLETED || target == REJECTED;
            case REJECTED -> target == REGISTERED;
            case COMPLETED -> false;
        };
    }
}
