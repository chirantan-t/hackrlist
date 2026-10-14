package com.hackathon.management.repository;

import com.hackathon.management.entity.ProgressStatus;
import com.hackathon.management.entity.RegistrationStatus;
import com.hackathon.management.entity.Team;
import com.hackathon.management.entity.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByTeamCode(String teamCode);

    boolean existsByTeamCode(String teamCode);

    List<Team> findByTeamLead(String teamLead);

    long countByRegistrationStatus(RegistrationStatus registrationStatus);

    long countByTrack(Track track);

    // Dynamic Filter Query
    @Query("SELECT t FROM Team t WHERE " +
           "(:status IS NULL OR t.registrationStatus = :status) AND " +
           "(:track IS NULL OR t.track = :track)")
    Page<Team> findAllWithFilters(@Param("status") RegistrationStatus status,
                                  @Param("track") Track track,
                                  Pageable pageable);

    // Dynamic Search Query
    @Query("SELECT t FROM Team t WHERE " +
           "(LOWER(t.teamName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(t.teamCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(t.projectTitle) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(t.teamLead) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR t.registrationStatus = :status) AND " +
           "(:track IS NULL OR t.track = :track)")
    Page<Team> searchTeams(@Param("query") String query,
                           @Param("status") RegistrationStatus status,
                           @Param("track") Track track,
                           Pageable pageable);

    // Recent 5 updated teams for dashboard
    List<Team> findTop5ByOrderByUpdatedAtDesc();
}
