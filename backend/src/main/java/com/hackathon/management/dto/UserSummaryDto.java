package com.hackathon.management.dto;

import com.hackathon.management.entity.Role;

public class UserSummaryDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;

    public UserSummaryDto() {
    }

    public UserSummaryDto(Long id, String username, String fullName, String email, Role role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
