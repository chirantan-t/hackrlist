package com.hackathon.management.dto;

import java.util.List;

public class DashboardSummaryDto {
    private long totalTeams;
    private long draftTeams;
    private long registeredTeams;
    private long approvedTeams;
    private long inProgressTeams;
    private long submittedTeams;
    private long completedTeams;
    private long rejectedTeams;
    private long totalMembers;
    private List<TeamDto> recentTeams;

    public DashboardSummaryDto() {
    }

    public long getTotalTeams() {
        return totalTeams;
    }

    public void setTotalTeams(long totalTeams) {
        this.totalTeams = totalTeams;
    }

    public long getDraftTeams() {
        return draftTeams;
    }

    public void setDraftTeams(long draftTeams) {
        this.draftTeams = draftTeams;
    }

    public long getRegisteredTeams() {
        return registeredTeams;
    }

    public void setRegisteredTeams(long registeredTeams) {
        this.registeredTeams = registeredTeams;
    }

    public long getApprovedTeams() {
        return approvedTeams;
    }

    public void setApprovedTeams(long approvedTeams) {
        this.approvedTeams = approvedTeams;
    }

    public long getInProgressTeams() {
        return inProgressTeams;
    }

    public void setInProgressTeams(long inProgressTeams) {
        this.inProgressTeams = inProgressTeams;
    }

    public long getSubmittedTeams() {
        return submittedTeams;
    }

    public void setSubmittedTeams(long submittedTeams) {
        this.submittedTeams = submittedTeams;
    }

    public long getCompletedTeams() {
        return completedTeams;
    }

    public void setCompletedTeams(long completedTeams) {
        this.completedTeams = completedTeams;
    }

    public long getRejectedTeams() {
        return rejectedTeams;
    }

    public void setRejectedTeams(long rejectedTeams) {
        this.rejectedTeams = rejectedTeams;
    }

    public long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public List<TeamDto> getRecentTeams() {
        return recentTeams;
    }

    public void setRecentTeams(List<TeamDto> recentTeams) {
        this.recentTeams = recentTeams;
    }
}
