package com.carlssonstudio.api.controller;

import com.carlssonstudio.api.dto.*;
import com.carlssonstudio.api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>>
            stats() {
        return ResponseEntity.ok(ApiResponse.ok(
            "Stats retrieved", dashboardService.stats()));
    }

    @GetMapping("/foundations")
    public ResponseEntity<ApiResponse<List<FoundationPopularityResponse>>> foundationPopularity() {
        return ResponseEntity.ok(ApiResponse.ok(
            "Foundation popularity retrieved",
            dashboardService.foundationPopularity()));
    }

    @GetMapping("/timeline")
    public ResponseEntity<ApiResponse<List<TimelinePointResponse>>>
            timeline(@RequestParam(defaultValue = "30")
                     int days) {
        return ResponseEntity.ok(ApiResponse.ok(
            "Timeline retrieved",
            dashboardService.timeline(days)));
    }
}