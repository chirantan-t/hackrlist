package com.hackathon.management.dto;

import com.hackathon.management.entity.Track;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TeamCreateRequest {

    @NotBlank(message = "Team name cannot be blank")
    @Size(min = 2, max = 100, message = "Team name must be between 2 and 100 characters")
    private String teamName;

    @NotBlank(message = "Team code cannot be blank")
    @Size(min = 3, max = 30, message = "Team code must be between 3 and 30 characters")
    private String teamCode;

    @NotBlank(message = "Project title cannot be blank")
    @Size(min = 3, max = 200, message = "Project title must be between 3 and 200 characters")
    private String projectTitle;

    @NotBlank(message = "Project description cannot be blank")
    private String projectDescription;

    @NotBlank(message = "Team lead name cannot be blank")
    private String teamLead;

    @NotNull(message = "Hackathon track is required")
    private Track track;

    public TeamCreateRequest() {
    }

    public TeamCreateRequest(String teamName, String teamCode, String projectTitle, String projectDescription, String teamLead, Track track) {
        this.teamName = teamName;
        this.teamCode = teamCode;
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
        this.teamLead = teamLead;
        this.track = track;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
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
}
