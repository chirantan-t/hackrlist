package com.hackathon.management.controller;

import com.hackathon.management.dto.TeamMemberDto;
import com.hackathon.management.dto.TeamMemberRequest;
import com.hackathon.management.service.TeamMemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams/{id}/members")
public class TeamMemberController {

    private final TeamMemberService memberService;

    public TeamMemberController(TeamMemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<TeamMemberDto>> getTeamMembers(@PathVariable Long id) {
        List<TeamMemberDto> members = memberService.getMembersByTeamId(id);
        return ResponseEntity.ok(members);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'TEAM_LEAD')")
    public ResponseEntity<TeamMemberDto> addTeamMember(@PathVariable Long id, @Valid @RequestBody TeamMemberRequest request) {
        TeamMemberDto created = memberService.addMember(id, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'TEAM_LEAD')")
    public ResponseEntity<TeamMemberDto> updateTeamMember(
            @PathVariable Long id,
            @PathVariable Long memberId,
            @Valid @RequestBody TeamMemberRequest request) {
        TeamMemberDto updated = memberService.updateMember(id, memberId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER', 'TEAM_LEAD')")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable Long id, @PathVariable Long memberId) {
        memberService.deleteMember(id, memberId);
        return ResponseEntity.noContent().build();
    }
}
