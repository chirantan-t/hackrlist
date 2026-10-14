package com.hackathon.management.controller;

import com.hackathon.management.dto.DashboardSummaryDto;
import com.hackathon.management.dto.StatusDistributionDto;
import com.hackathon.management.dto.TrackDistributionDto;
import com.hackathon.management.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary() {
        DashboardSummaryDto summary = dashboardService.getSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/status-distribution")
    public ResponseEntity<List<StatusDistributionDto>> getStatusDistribution() {
        List<StatusDistributionDto> distribution = dashboardService.getStatusDistribution();
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/track-distribution")
    public ResponseEntity<List<TrackDistributionDto>> getTrackDistribution() {
        List<TrackDistributionDto> distribution = dashboardService.getTrackDistribution();
        return ResponseEntity.ok(distribution);
    }
}
