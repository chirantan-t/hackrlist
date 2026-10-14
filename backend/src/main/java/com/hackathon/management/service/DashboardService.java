package com.hackathon.management.service;

import com.hackathon.management.dto.DashboardSummaryDto;
import com.hackathon.management.dto.StatusDistributionDto;
import com.hackathon.management.dto.TrackDistributionDto;
import com.hackathon.management.entity.RegistrationStatus;
import com.hackathon.management.entity.Track;
import com.hackathon.management.repository.TeamMemberRepository;
import com.hackathon.management.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository memberRepository;
    private final TeamService teamService;

    public DashboardService(TeamRepository teamRepository, TeamMemberRepository memberRepository, TeamService teamService) {
        this.teamRepository = teamRepository;
        this.memberRepository = memberRepository;
        this.teamService = teamService;
    }

    public DashboardSummaryDto getSummary() {
        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setTotalTeams(teamRepository.count());
        summary.setDraftTeams(teamRepository.countByRegistrationStatus(RegistrationStatus.DRAFT));
        summary.setRegisteredTeams(teamRepository.countByRegistrationStatus(RegistrationStatus.REGISTERED));
        summary.setApprovedTeams(teamRepository.countByRegistrationStatus(RegistrationStatus.APPROVED));
        summary.setInProgressTeams(teamRepository.countByRegistrationStatus(RegistrationStatus.IN_PROGRESS));
        summary.setSubmittedTeams(teamRepository.countByRegistrationStatus(RegistrationStatus.SUBMITTED));
        summary.setCompletedTeams(teamRepository.countByRegistrationStatus(RegistrationStatus.COMPLETED));
        summary.setRejectedTeams(teamRepository.countByRegistrationStatus(RegistrationStatus.REJECTED));
        summary.setTotalMembers(memberRepository.count());

        summary.setRecentTeams(teamRepository.findTop5ByOrderByUpdatedAtDesc().stream()
                .map(teamService::convertToDto)
                .collect(Collectors.toList()));

        return summary;
    }

    public List<StatusDistributionDto> getStatusDistribution() {
        List<StatusDistributionDto> distribution = new ArrayList<>();
        for (RegistrationStatus status : RegistrationStatus.values()) {
            long count = teamRepository.countByRegistrationStatus(status);
            distribution.add(new StatusDistributionDto(status, formatLabel(status.name()), count));
        }
        return distribution;
    }

    public List<TrackDistributionDto> getTrackDistribution() {
        List<TrackDistributionDto> distribution = new ArrayList<>();
        for (Track track : Track.values()) {
            long count = teamRepository.countByTrack(track);
            distribution.add(new TrackDistributionDto(track, formatLabel(track.name()), count));
        }
        return distribution;
    }

    private String formatLabel(String enumName) {
        String[] words = enumName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)))
                  .append(w.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
