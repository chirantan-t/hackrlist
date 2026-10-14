package com.hackathon.management.service;

import com.hackathon.management.dto.*;
import com.hackathon.management.entity.*;
import com.hackathon.management.exception.DuplicateResourceException;
import com.hackathon.management.exception.InvalidWorkflowException;
import com.hackathon.management.exception.ResourceNotFoundException;
import com.hackathon.management.exception.UnauthorizedActionException;
import com.hackathon.management.repository.TeamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public TeamDto createTeam(TeamCreateRequest request) {
        if (teamRepository.existsByTeamCode(request.getTeamCode())) {
            throw new DuplicateResourceException("Team code '" + request.getTeamCode() + "' is already registered");
        }

        Team team = new Team(
                request.getTeamName().trim(),
                request.getTeamCode().trim().toUpperCase(),
                request.getProjectTitle().trim(),
                request.getProjectDescription().trim(),
                request.getTeamLead().trim(),
                request.getTrack()
        );

        Team savedTeam = teamRepository.save(team);
        return convertToDto(savedTeam);
    }

    @Transactional(readOnly = true)
    public TeamDto getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
        return convertToDto(team);
    }

    @Transactional(readOnly = true)
    public TeamDto getTeamByCode(String teamCode) {
        Team team = teamRepository.findByTeamCode(teamCode.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with code: " + teamCode));
        return convertToDto(team);
    }

    @Transactional(readOnly = true)
    public Page<TeamDto> getTeams(Pageable pageable, RegistrationStatus status, Track track) {
        Page<Team> teams = teamRepository.findAllWithFilters(status, track, pageable);
        return teams.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<TeamDto> searchTeams(String query, RegistrationStatus status, Track track, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return getTeams(pageable, status, track);
        }
        Page<Team> teams = teamRepository.searchTeams(query.trim(), status, track, pageable);
        return teams.map(this::convertToDto);
    }

    public TeamDto updateTeam(Long id, TeamUpdateRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));

        validateTeamLeadAccess(team);

        team.setTeamName(request.getTeamName().trim());
        team.setProjectTitle(request.getProjectTitle().trim());
        team.setProjectDescription(request.getProjectDescription().trim());
        team.setTeamLead(request.getTeamLead().trim());
        team.setTrack(request.getTrack());
        if (request.getProgressStatus() != null) {
            team.setProgressStatus(request.getProgressStatus());
        }

        Team updatedTeam = teamRepository.save(team);
        return convertToDto(updatedTeam);
    }

    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team not found with ID: " + id);
        }
        teamRepository.deleteById(id);
    }

    public TeamDto updateTeamStatus(Long id, StatusChangeRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));

        RegistrationStatus currentStatus = team.getRegistrationStatus();
        RegistrationStatus targetStatus = request.getStatus();

        // 1. Workflow transition validation
        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new InvalidWorkflowException(
                    "Invalid status transition from " + currentStatus + " to " + targetStatus +
                    ". Permitted workflow: DRAFT -> REGISTERED -> APPROVED -> IN_PROGRESS -> SUBMITTED -> COMPLETED (or REJECTED)."
            );
        }

        // 2. Role-based workflow authorization
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOrganizer = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER"));

        if (!isAdmin && !isOrganizer) {
            // Team Lead can only submit for review
            boolean isAllowedLeadAction = (currentStatus == RegistrationStatus.DRAFT && targetStatus == RegistrationStatus.REGISTERED) ||
                                          (currentStatus == RegistrationStatus.IN_PROGRESS && targetStatus == RegistrationStatus.SUBMITTED);
            if (!isAllowedLeadAction) {
                throw new UnauthorizedActionException("Only Organizers or Administrators can approve, reject, or complete teams.");
            }
        }

        team.setRegistrationStatus(targetStatus);
        Team updatedTeam = teamRepository.save(team);
        return convertToDto(updatedTeam);
    }

    private void validateTeamLeadAccess(Team team) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return;
        }
        boolean isLeadOnly = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEAM_LEAD")) &&
                auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_ORGANIZER"));

        if (isLeadOnly && !auth.getName().equalsIgnoreCase(team.getTeamLead())) {
            throw new UnauthorizedActionException("You are only permitted to manage your own team: " + team.getTeamLead());
        }
    }

    public TeamDto convertToDto(Team team) {
        TeamDto dto = new TeamDto();
        dto.setId(team.getId());
        dto.setTeamName(team.getTeamName());
        dto.setTeamCode(team.getTeamCode());
        dto.setProjectTitle(team.getProjectTitle());
        dto.setProjectDescription(team.getProjectDescription());
        dto.setTeamLead(team.getTeamLead());
        dto.setTrack(team.getTrack());
        dto.setRegistrationStatus(team.getRegistrationStatus());
        dto.setProgressStatus(team.getProgressStatus());
        dto.setMemberCount(team.getMemberCount());
        dto.setCreatedAt(team.getCreatedAt());
        dto.setUpdatedAt(team.getUpdatedAt());

        if (team.getMembers() != null) {
            dto.setMembers(team.getMembers().stream().map(m -> new TeamMemberDto(
                    m.getId(),
                    team.getId(),
                    m.getFullName(),
                    m.getEmail(),
                    m.getCollege(),
                    m.getDepartment(),
                    m.getTeamRole(),
                    m.getCreatedAt()
            )).collect(Collectors.toList()));
        }

        return dto;
    }
}
