package com.hackathon.management.dto;

import com.hackathon.management.entity.RegistrationStatus;
import jakarta.validation.constraints.NotNull;

public class StatusChangeRequest {

    @NotNull(message = "New registration status is required")
    private RegistrationStatus status;

    private String remarks;

    public StatusChangeRequest() {
    }

    public StatusChangeRequest(RegistrationStatus status, String remarks) {
        this.status = status;
        this.remarks = remarks;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
