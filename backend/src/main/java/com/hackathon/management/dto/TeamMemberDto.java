package com.hackathon.management.dto;

import com.hackathon.management.entity.TeamRole;
import java.time.LocalDateTime;

public class TeamMemberDto {
    private Long id;
    private Long teamId;
    private String fullName;
    private String email;
    private String college;
    private String department;
    private TeamRole teamRole;
    private LocalDateTime createdAt;

    public TeamMemberDto() {
    }

    public TeamMemberDto(Long id, Long teamId, String fullName, String email, String college, String department, TeamRole teamRole, LocalDateTime createdAt) {
        this.id = id;
        this.teamId = teamId;
        this.fullName = fullName;
        this.email = email;
        this.college = college;
        this.department = department;
        this.teamRole = teamRole;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public TeamRole getTeamRole() {
        return teamRole;
    }

    public void setTeamRole(TeamRole teamRole) {
        this.teamRole = teamRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
