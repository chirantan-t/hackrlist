package com.hackathon.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.management.dto.StatusChangeRequest;
import com.hackathon.management.dto.TeamCreateRequest;
import com.hackathon.management.entity.RegistrationStatus;
import com.hackathon.management.entity.Role;
import com.hackathon.management.entity.Team;
import com.hackathon.management.entity.Track;
import com.hackathon.management.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TeamControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void cleanDatabase() {
        teamRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /api/teams should create team and return 201 Created")
    void testCreateTeam_Success() throws Exception {
        TeamCreateRequest request = new TeamCreateRequest(
                "FinTech Innovators",
                "HTM-FT-101",
                "Algorithmic Micro-Lending",
                "Automated risk scoring using graph analytics",
                "admin",
                Track.FINTECH
        );

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamName").value("FinTech Innovators"))
                .andExpect(jsonPath("$.teamCode").value("HTM-FT-101"))
                .andExpect(jsonPath("$.registrationStatus").value("REGISTERED"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /api/teams should return paginated list of teams")
    void testGetTeams_Paginated() throws Exception {
        Team team1 = new Team("Alpha", "HTM-01", "Project 1", "Desc 1", "lead1", Track.AI_ML);
        Team team2 = new Team("Beta", "HTM-02", "Project 2", "Desc 2", "lead2", Track.WEB3_BLOCKCHAIN);
        teamRepository.save(team1);
        teamRepository.save(team2);

        mockMvc.perform(get("/api/teams?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PATCH /api/teams/{id}/status should enforce status workflow and reject invalid jump")
    void testUpdateTeamStatus_WorkflowValidation() throws Exception {
        Team team = new Team("Gamma", "HTM-03", "Project 3", "Desc 3", "lead3", Track.HEALTHTECH);
        team.setRegistrationStatus(RegistrationStatus.DRAFT);
        Team saved = teamRepository.save(team);

        // Attempt illegal jump DRAFT -> COMPLETED
        StatusChangeRequest invalidReq = new StatusChangeRequest(RegistrationStatus.COMPLETED, "Illegal");

        mockMvc.perform(patch("/api/teams/" + saved.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        // Valid transition DRAFT -> REGISTERED
        StatusChangeRequest validReq = new StatusChangeRequest(RegistrationStatus.REGISTERED, "Submitted for review");

        mockMvc.perform(patch("/api/teams/" + saved.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationStatus").value("REGISTERED"));
    }

    @Test
    @WithMockUser(username = "lead", roles = {"TEAM_LEAD"})
    @DisplayName("DELETE /api/teams/{id} should be forbidden for TEAM_LEAD")
    void testDeleteTeam_ForbiddenForLead() throws Exception {
        Team team = new Team("Delta", "HTM-04", "Project 4", "Desc 4", "lead", Track.OPEN_INNOVATION);
        Team saved = teamRepository.save(team);

        mockMvc.perform(delete("/api/teams/" + saved.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE /api/teams/{id} should succeed with 204 No Content for ADMIN")
    void testDeleteTeam_SuccessForAdmin() throws Exception {
        Team team = new Team("Epsilon", "HTM-05", "Project 5", "Desc 5", "admin", Track.OPEN_INNOVATION);
        Team saved = teamRepository.save(team);

        mockMvc.perform(delete("/api/teams/" + saved.getId()))
                .andExpect(status().isNoContent());
    }
}
