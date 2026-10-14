package com.hackathon.management.dto;

import com.hackathon.management.entity.ProgressStatus;
import com.hackathon.management.entity.Track;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TeamUpdateRequest {

    @NotBlank(message = "Team name cannot be blank")
    @Size(min = 2, max = 100, message = "Team name must be between 2 and 100 characters")
    private String teamName;

    @NotBlank(message = "Project title cannot be blank")
    @Size(min = 3, max = 200, message = "Project title must be between 3 and 200 characters")
    private String projectTitle;

    @NotBlank(message = "Project description cannot be blank")
    private String projectDescription;

    @NotBlank(message = "Team lead name cannot be blank")
    private String teamLead;

    @NotNull(message = "Hackathon track is required")
    private Track track;

    private ProgressStatus progressStatus;

    public TeamUpdateRequest() {
    }

    public TeamUpdateRequest(String teamName, String projectTitle, String projectDescription, String teamLead, Track track, ProgressStatus progressStatus) {
        this.teamName = teamName;
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
        this.teamLead = teamLead;
        this.track = track;
        this.progressStatus = progressStatus;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getTeamLead() {
        return teamLead;
    }

    public void setTeamLead(String teamLead) {
        this.teamLead = teamLead;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public ProgressStatus getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(ProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }
}
