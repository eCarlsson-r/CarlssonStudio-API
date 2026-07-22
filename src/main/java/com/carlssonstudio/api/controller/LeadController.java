package com.carlssonstudio.api.controller;

import com.carlssonstudio.api.dto.*;
import com.carlssonstudio.api.entity.LeadStatus;
import com.carlssonstudio.api.service.LeadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class LeadController {

    private final LeadService leadService;

    // Public — called by Next.js questionnaire
    @PostMapping
    public ResponseEntity<ApiResponse<LeadResponse>> submit(
            @Valid @RequestBody LeadRequest request,
            HttpServletRequest httpRequest) {
        LeadResponse response = leadService.submit(
            request, clientIp(httpRequest),
            httpRequest.getHeader("User-Agent"));
        return ResponseEntity.ok(
            ApiResponse.ok("Submission received", response));
    }

    /** Behind a proxy/load balancer, the real visitor IP is in
     *  X-Forwarded-For (first hop); fall back to the socket address. */
    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    // Admin only — secured by Spring Security
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeadResponse>>> findAll() {
        return ResponseEntity.ok(
            ApiResponse.ok("Leads retrieved", leadService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> findById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.ok("Lead retrieved", leadService.findById(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<LeadResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam LeadStatus status) {
        return ResponseEntity.ok(
            ApiResponse.ok("Status updated",
                leadService.updateStatus(id, status)));
    }
}