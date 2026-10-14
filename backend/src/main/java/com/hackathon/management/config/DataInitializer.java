package com.hackathon.management.config;

import com.hackathon.management.entity.*;
import com.hackathon.management.repository.TeamMemberRepository;
import com.hackathon.management.repository.TeamRepository;
import com.hackathon.management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "app.bootstrap.seed-data", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           TeamRepository teamRepository,
                           TeamMemberRepository memberRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedTeams();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            User admin = new User("admin", passwordEncoder.encode("Admin@123"), "Lead Administrator", "admin@hackathon.dev", Role.ADMIN);
            User organizer = new User("organizer", passwordEncoder.encode("Organizer@123"), "Event Coordinator", "organizer@hackathon.dev", Role.ORGANIZER);
            User teamLead = new User("teamlead", passwordEncoder.encode("Lead@123"), "Alex Rivera", "alex.lead@hackathon.dev", Role.TEAM_LEAD);

            userRepository.saveAll(List.of(admin, organizer, teamLead));
        }
    }

    private void seedTeams() {
        if (teamRepository.count() == 0) {
            // Team 1: AI_ML - IN_PROGRESS
            Team team1 = new Team(
                    "Neural Navigators",
                    "HTM-AI-01",
                    "Autonomous Navigation for First Responders",
                    "Edge compute drone swarms mapping disaster zones in real time using SLAM and visual transformers.",
                    "teamlead",
                    Track.AI_ML
            );
            team1.setRegistrationStatus(RegistrationStatus.IN_PROGRESS);
            team1.setProgressStatus(ProgressStatus.IN_DEVELOPMENT);

            // Team 2: FINTECH - APPROVED
            Team team2 = new Team(
                    "Ledger Leap",
                    "HTM-FT-02",
                    "Decentralized Micro-Credit Scoring",
                    "Privacy-preserving zero-knowledge proof credit scoring system for unbanked micro-entrepreneurs.",
                    "marcus_fin",
                    Track.FINTECH
            );
            team2.setRegistrationStatus(RegistrationStatus.APPROVED);
            team2.setProgressStatus(ProgressStatus.IN_DESIGN);

            // Team 3: HEALTHTECH - SUBMITTED
            Team team3 = new Team(
                    "BioPulse Diagnostics",
                    "HTM-HT-03",
                    "Photoplethysmography Anomaly Detection",
                    "Wearable biosensor platform providing 15-minute early warning flags for cardiovascular irregularities.",
                    "dr_elena",
                    Track.HEALTHTECH
            );
            team3.setRegistrationStatus(RegistrationStatus.SUBMITTED);
            team3.setProgressStatus(ProgressStatus.SUBMITTED);

            // Team 4: WEB3_BLOCKCHAIN - REGISTERED
            Team team4 = new Team(
                    "EtherGuardians",
                    "HTM-W3-04",
                    "Cross-Chain Identity Verifier",
                    "Self-sovereign cryptographic credential management across EVM and Solana ecosystems.",
                    "crypto_satoshi",
                    Track.WEB3_BLOCKCHAIN
            );
            team4.setRegistrationStatus(RegistrationStatus.REGISTERED);
            team4.setProgressStatus(ProgressStatus.NOT_STARTED);

            // Team 5: CYBERSECURITY - DRAFT
            Team team5 = new Team(
                    "ZeroDay Sentinel",
                    "HTM-CS-05",
                    "eBPF Kernel Anomaly Radar",
                    "Lightweight Linux kernel observability agent intercepting privilege escalation attacks.",
                    "sec_ghost",
                    Track.CYBERSECURITY
            );
            team5.setRegistrationStatus(RegistrationStatus.DRAFT);
            team5.setProgressStatus(ProgressStatus.NOT_STARTED);

            // Team 6: OPEN_INNOVATION - COMPLETED
            Team team6 = new Team(
                    "AquaSense Grid",
                    "HTM-OI-06",
                    "Smart Urban Water Distribution Monitor",
                    "Low-power LoRaWAN sensor network locating micro-leaks and potable water contamination.",
                    "priya_iot",
                    Track.OPEN_INNOVATION
            );
            team6.setRegistrationStatus(RegistrationStatus.COMPLETED);
            team6.setProgressStatus(ProgressStatus.TESTED);

            teamRepository.saveAll(List.of(team1, team2, team3, team4, team5, team6));

            // Seed members for Team 1
            TeamMember m1 = new TeamMember("Alex Rivera", "alex.lead@hackathon.dev", "MIT", "Robotics", TeamRole.FULLSTACK);
            TeamMember m2 = new TeamMember("Chloe Zhang", "chloe.z@mit.edu", "MIT", "Electrical Engineering", TeamRole.DATA_SCIENTIST);
            TeamMember m3 = new TeamMember("Liam Vance", "liam.v@stanford.edu", "Stanford", "Computer Science", TeamRole.BACKEND);
            team1.addMember(m1);
            team1.addMember(m2);
            team1.addMember(m3);
            memberRepository.saveAll(List.of(m1, m2, m3));

            // Seed members for Team 2
            TeamMember m4 = new TeamMember("Marcus Thorne", "marcus.t@oxford.ac.uk", "Oxford", "Finance & CS", TeamRole.FULLSTACK);
            TeamMember m5 = new TeamMember("Aisha Khan", "aisha.k@cambridge.ac.uk", "Cambridge", "Mathematics", TeamRole.UI_UX);
            team2.addMember(m4);
            team2.addMember(m5);
            memberRepository.saveAll(List.of(m4, m5));

            // Seed members for Team 3
            TeamMember m6 = new TeamMember("Elena Rostova", "elena.r@harvard.edu", "Harvard Medical", "Biomedical Eng", TeamRole.DATA_SCIENTIST);
            TeamMember m7 = new TeamMember("Daniel Kim", "daniel.k@berkeley.edu", "UC Berkeley", "Computer Science", TeamRole.DEVOPS);
            team3.addMember(m6);
            team3.addMember(m7);
            memberRepository.saveAll(List.of(m6, m7));
        }
    }
}
