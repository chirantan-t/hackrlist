package com.hackathon.management.dto;

import com.hackathon.management.entity.RegistrationStatus;

public class StatusDistributionDto {
    private RegistrationStatus status;
    private String label;
    private long count;

    public StatusDistributionDto() {
    }

    public StatusDistributionDto(RegistrationStatus status, String label, long count) {
        this.status = status;
        this.label = label;
        this.count = count;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
