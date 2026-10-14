package com.hackathon.management.controller;

import com.hackathon.management.dto.*;
import com.hackathon.management.entity.RegistrationStatus;
import com.hackathon.management.entity.Track;
import com.hackathon.management.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<Page<TeamDto>> getTeams(
            @RequestParam(required = false) RegistrationStatus status,
            @RequestParam(required = false) Track track,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TeamDto> teams = teamService.getTeams(pageable, status, track);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TeamDto>> searchTeams(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) RegistrationStatus status,
            @RequestParam(required = false) Track track,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TeamDto> teams = teamService.searchTeams(q, status, track, pageable);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable Long id) {
        TeamDto team = teamService.getTeamById(id);
        return ResponseEntity.ok(team);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'TEAM_LEAD')")
    public ResponseEntity<TeamDto> createTeam(@Valid @RequestBody TeamCreateRequest request) {
        TeamDto created = teamService.createTeam(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'TEAM_LEAD')")
    public ResponseEntity<TeamDto> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamUpdateRequest request) {
        TeamDto updated = teamService.updateTeam(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'TEAM_LEAD')")
    public ResponseEntity<TeamDto> updateTeamStatus(@PathVariable Long id, @Valid @RequestBody StatusChangeRequest request) {
        TeamDto updated = teamService.updateTeamStatus(id, request);
        return ResponseEntity.ok(updated);
    }
}
