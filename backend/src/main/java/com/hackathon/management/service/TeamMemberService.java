package com.hackathon.management.service;

import com.hackathon.management.dto.TeamMemberDto;
import com.hackathon.management.dto.TeamMemberRequest;
import com.hackathon.management.entity.Team;
import com.hackathon.management.entity.TeamMember;
import com.hackathon.management.exception.DuplicateResourceException;
import com.hackathon.management.exception.ResourceNotFoundException;
import com.hackathon.management.repository.TeamMemberRepository;
import com.hackathon.management.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamMemberService {

    private final TeamMemberRepository memberRepository;
    private final TeamRepository teamRepository;

    public TeamMemberService(TeamMemberRepository memberRepository, TeamRepository teamRepository) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public List<TeamMemberDto> getMembersByTeamId(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team not found with ID: " + teamId);
        }
        return memberRepository.findByTeamId(teamId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TeamMemberDto addMember(Long teamId, TeamMemberRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));

        String email = request.getEmail().trim().toLowerCase();
        if (memberRepository.existsByTeamIdAndEmail(teamId, email)) {
            throw new DuplicateResourceException("Member with email '" + email + "' is already enrolled in this team");
        }

        TeamMember member = new TeamMember(
                request.getFullName().trim(),
                email,
                request.getCollege().trim(),
                request.getDepartment().trim(),
                request.getTeamRole()
        );
        member.setTeam(team);
        TeamMember saved = memberRepository.save(member);
        return convertToDto(saved);
    }

    public TeamMemberDto updateMember(Long teamId, Long memberId, TeamMemberRequest request) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team not found with ID: " + teamId);
        }
        TeamMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));

        if (!member.getTeam().getId().equals(teamId)) {
            throw new ResourceNotFoundException("Member ID " + memberId + " does not belong to team " + teamId);
        }

        String email = request.getEmail().trim().toLowerCase();
        if (!member.getEmail().equalsIgnoreCase(email) && memberRepository.existsByTeamIdAndEmail(teamId, email)) {
            throw new DuplicateResourceException("Member with email '" + email + "' is already enrolled in this team");
        }

        member.setFullName(request.getFullName().trim());
        member.setEmail(email);
        member.setCollege(request.getCollege().trim());
        member.setDepartment(request.getDepartment().trim());
        member.setTeamRole(request.getTeamRole());

        TeamMember updated = memberRepository.save(member);
        return convertToDto(updated);
    }

    public void deleteMember(Long teamId, Long memberId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team not found with ID: " + teamId);
        }
        TeamMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));

        if (!member.getTeam().getId().equals(teamId)) {
            throw new ResourceNotFoundException("Member ID " + memberId + " does not belong to team " + teamId);
        }

        memberRepository.delete(member);
    }

    private TeamMemberDto convertToDto(TeamMember member) {
        return new TeamMemberDto(
                member.getId(),
                member.getTeam().getId(),
                member.getFullName(),
                member.getEmail(),
                member.getCollege(),
                member.getDepartment(),
                member.getTeamRole(),
                member.getCreatedAt()
        );
    }
}
