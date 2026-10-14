package com.hackathon.management.dto;

import com.hackathon.management.entity.TeamRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TeamMemberRequest {

    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "College/institution cannot be blank")
    private String college;

    @NotBlank(message = "Department cannot be blank")
    private String department;

    @NotNull(message = "Team role is required")
    private TeamRole teamRole;

    public TeamMemberRequest() {
    }

    public TeamMemberRequest(String fullName, String email, String college, String department, TeamRole teamRole) {
        this.fullName = fullName;
        this.email = email;
        this.college = college;
        this.department = department;
        this.teamRole = teamRole;
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
}
