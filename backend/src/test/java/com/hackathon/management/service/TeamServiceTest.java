package com.hackathon.management.service;

import com.hackathon.management.dto.StatusChangeRequest;
import com.hackathon.management.dto.TeamCreateRequest;
import com.hackathon.management.dto.TeamDto;
import com.hackathon.management.entity.ProgressStatus;
import com.hackathon.management.entity.RegistrationStatus;
import com.hackathon.management.entity.Team;
import com.hackathon.management.entity.Track;
import com.hackathon.management.exception.DuplicateResourceException;
import com.hackathon.management.exception.InvalidWorkflowException;
import com.hackathon.management.exception.ResourceNotFoundException;
import com.hackathon.management.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    @BeforeEach
    void setUpSecurity() {
        // Mock Admin authentication context
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Should create team successfully when team code is unique")
    void testCreateTeam_Success() {
        TeamCreateRequest request = new TeamCreateRequest(
                "Cyber Titans", "HTM-CY-01", "Threat Sentinel", "Zero trust scanner", "lead_user", Track.CYBERSECURITY
        );

        when(teamRepository.existsByTeamCode("HTM-CY-01")).thenReturn(false);

        Team savedTeam = new Team(request.getTeamName(), request.getTeamCode(), request.getProjectTitle(),
                request.getProjectDescription(), request.getTeamLead(), request.getTrack());
        savedTeam.setId(10L);
        when(teamRepository.save(any(Team.class))).thenReturn(savedTeam);

        TeamDto result = teamService.createTeam(request);

        assertNotNull(result);
        assertEquals("Cyber Titans", result.getTeamName());
        assertEquals("HTM-CY-01", result.getTeamCode());
        assertEquals(RegistrationStatus.REGISTERED, result.getRegistrationStatus());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when team code already exists")
    void testCreateTeam_DuplicateCode_ThrowsException() {
        TeamCreateRequest request = new TeamCreateRequest(
                "Duplicate Titans", "HTM-CY-01", "Threat Sentinel", "Zero trust scanner", "lead_user", Track.CYBERSECURITY
        );

        when(teamRepository.existsByTeamCode("HTM-CY-01")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> teamService.createTeam(request));
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    @DisplayName("Should transition team status according to allowed workflow")
    void testUpdateTeamStatus_ValidTransition_Success() {
        Team team = new Team("AI Lab", "HTM-AI-99", "Vision AI", "Desc", "lead", Track.AI_ML);
        team.setId(1L);
        team.setRegistrationStatus(RegistrationStatus.REGISTERED);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        StatusChangeRequest request = new StatusChangeRequest(RegistrationStatus.APPROVED, "Verified");
        TeamDto updated = teamService.updateTeamStatus(1L, request);

        assertEquals(RegistrationStatus.APPROVED, updated.getRegistrationStatus());
        verify(teamRepository, times(1)).save(team);
    }

    @Test
    @DisplayName("Should reject illegal workflow transitions with InvalidWorkflowException")
    void testUpdateTeamStatus_InvalidTransition_ThrowsException() {
        Team team = new Team("AI Lab", "HTM-AI-99", "Vision AI", "Desc", "lead", Track.AI_ML);
        team.setId(1L);
        team.setRegistrationStatus(RegistrationStatus.DRAFT);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        // Attempt illegal transition DRAFT -> COMPLETED
        StatusChangeRequest request = new StatusChangeRequest(RegistrationStatus.COMPLETED, "Illegal jump");

        assertThrows(InvalidWorkflowException.class, () -> teamService.updateTeamStatus(1L, request));
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when team ID does not exist")
    void testGetTeamById_NotFound() {
        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> teamService.getTeamById(999L));
    }
}
