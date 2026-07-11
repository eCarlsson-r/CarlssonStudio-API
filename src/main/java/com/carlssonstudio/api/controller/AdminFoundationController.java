package com.carlssonstudio.api.controller;

import com.carlssonstudio.api.dto.*;
import com.carlssonstudio.api.service.FoundationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/foundations")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class AdminFoundationController {

    private final FoundationService foundationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FoundationResponse>>>
            findAll() {
        return ResponseEntity.ok(ApiResponse.ok(
            "Foundations retrieved",
            foundationService.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FoundationResponse>>
            create(@Valid @RequestBody FoundationRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
            "Foundation created",
            foundationService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoundationResponse>>
            update(@PathVariable Long id,
                   @Valid @RequestBody FoundationRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
            "Foundation updated",
            foundationService.update(id, req)));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<FoundationResponse>>
            toggle(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
            "Foundation status toggled",
            foundationService.toggleActive(id)));
    }
}