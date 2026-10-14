package com.hackathon.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Team name is required")
    @Column(name = "team_name", nullable = false, length = 100)
    private String teamName;

    @NotBlank(message = "Unique team code is required")
    @Column(name = "team_code", nullable = false, unique = true, length = 30)
    private String teamCode;

    @NotBlank(message = "Project title is required")
    @Column(name = "project_title", nullable = false, length = 200)
    private String projectTitle;

    @NotBlank(message = "Project description is required")
    @Column(name = "project_description", nullable = false, columnDefinition = "TEXT")
    private String projectDescription;

    @NotBlank(message = "Team lead is required")
    @Column(name = "team_lead", nullable = false, length = 100)
    private String teamLead;

    @NotNull(message = "Track is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Track track;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false, length = 30)
    private RegistrationStatus registrationStatus = RegistrationStatus.REGISTERED;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "progress_status", nullable = false, length = 30)
    private ProgressStatus progressStatus = ProgressStatus.NOT_STARTED;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TeamMember> members = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Team() {
    }

    public Team(String teamName, String teamCode, String projectTitle, String projectDescription, String teamLead, Track track) {
        this.teamName = teamName;
        this.teamCode = teamCode;
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
        this.teamLead = teamLead;
        this.track = track;
        this.registrationStatus = RegistrationStatus.REGISTERED;
        this.progressStatus = ProgressStatus.NOT_STARTED;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.registrationStatus == null) {
            this.registrationStatus = RegistrationStatus.REGISTERED;
        }
        if (this.progressStatus == null) {
            this.progressStatus = ProgressStatus.NOT_STARTED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper method to add member
    public void addMember(TeamMember member) {
        members.add(member);
        member.setTeam(this);
    }

    // Helper method to remove member
    public void removeMember(TeamMember member) {
        members.remove(member);
        member.setTeam(null);
    }

    // Derived member count to ensure strict consistency
    @Transient
    public int getMemberCount() {
        return members != null ? members.size() : 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public ProgressStatus getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(ProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }

    public List<TeamMember> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMember> members) {
        this.members = members;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
