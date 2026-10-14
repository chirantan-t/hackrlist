package com.hackathon.management.repository;

import com.hackathon.management.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByTeamId(Long teamId);
    Optional<TeamMember> findByTeamIdAndEmail(Long teamId, String email);
    boolean existsByTeamIdAndEmail(Long teamId, String email);
    long countByTeamId(Long teamId);
    void deleteByTeamId(Long teamId);
}
