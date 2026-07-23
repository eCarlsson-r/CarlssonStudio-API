package com.carlssonstudio.api.controller;

import com.carlssonstudio.api.dto.*;
import com.carlssonstudio.api.service.FoundationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class ConfigController {

    private final FoundationService foundationService;
    private final MessageSource messageSource;

    @GetMapping("/foundations")
    public ResponseEntity<ApiResponse<List<FoundationResponse>>>
            activeFoundations(Locale locale) {
        return ResponseEntity.ok(ApiResponse.ok(
            messageSource.getMessage("api.config.foundations", null, locale),
            foundationService.findActive()));
    }

    @GetMapping("/questionnaire")
    public ResponseEntity<ApiResponse<Map<String, Object>>>
            questionnaire(Locale locale) {
        List<FoundationResponse> active =
            foundationService.findActive();

        // Derive all options from active foundations
        Set<String> industries = new TreeSet<>();
        Set<String> buildTypes = new TreeSet<>();
        Set<String> problems = new TreeSet<>();
        Set<String> features = new TreeSet<>();

        for (FoundationResponse f : active) {
            industries.add(f.getIndustry());
            industries.addAll(f.getRelatedIndustries());
            buildTypes.addAll(f.getBuildTypes());
            problems.addAll(f.getProblems());
            features.addAll(f.getFeatures());
        }

        Map<String, Object> config = new LinkedHashMap<>();
        config.put("industries", industries);
        config.put("buildTypes", buildTypes);
        config.put("problems", problems);
        config.put("features", features);
        config.put("companySizes",
            List.of("1-5", "5-20", "20-100", "100+"));

        return ResponseEntity.ok(ApiResponse.ok(
            messageSource.getMessage("api.config.questionnaire", null, locale),
            config));
    }
}