package com.carlssonstudio.api.controller;

import com.carlssonstudio.api.dto.*;
import com.carlssonstudio.api.entity.Proposal;
import com.carlssonstudio.api.service.ProposalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class ProposalController {

    private final ProposalService proposalService;

    // Admin — generate proposal for a lead
    @PostMapping
    public ResponseEntity<ApiResponse<ProposalResponse>> generate(
            @Valid @RequestBody ProposalRequest request)
            throws Exception {
        ProposalResponse response =
            proposalService.generate(request);
        return ResponseEntity.ok(
            ApiResponse.ok("Proposal generated", response));
    }

    // Admin — list proposals for a lead
    @GetMapping("/lead/{leadId}")
    public ResponseEntity<ApiResponse<List<ProposalResponse>>>
            findByLead(@PathVariable Long leadId) {
        return ResponseEntity.ok(ApiResponse.ok(
            "Proposals retrieved",
            proposalService.findByLeadId(leadId)));
    }

    // Public — download PDF by proposal ID
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(
            @PathVariable Long id) {
        Proposal proposal = proposalService.findEntityById(id);

        if (proposal.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(proposal.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        String filename = "Carlsson-Studio-Proposal-"
            + proposal.getFoundationName()
                .replace(" ", "-") + ".pdf";

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"")
            .body(resource);
    }
}