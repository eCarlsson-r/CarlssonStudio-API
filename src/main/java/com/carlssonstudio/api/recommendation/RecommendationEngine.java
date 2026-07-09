package com.carlssonstudio.api.recommendation;

import com.carlssonstudio.api.dto.LeadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationEngine {

    private final FoundationRegistry registry;

    // Weights — must sum to 100
    private static final int WEIGHT_INDUSTRY   = 40;
    private static final int WEIGHT_BUILD_TYPE = 25;
    private static final int WEIGHT_PROBLEMS   = 20;
    private static final int WEIGHT_FEATURES   = 15;

    public List<ScoringResult> recommend(LeadRequest request) {
        return registry.getAll()
                .stream()
                .map(f -> score(f, request))
                .sorted(Comparator
                    .comparingInt(ScoringResult::getScore).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    private ScoringResult score(Foundation f, LeadRequest req) {
        int industryScore   = scoreIndustry(f, req.getIndustry());
        int buildTypeScore  = scoreBuildType(f, req.getBuildType());
        int problemScore    = scoreList(
            f.getProblems(), req.getProblems());
        int featureScore    = scoreList(
            f.getFeatures(), req.getFeatures());

        int total = Math.round(
            (industryScore   * WEIGHT_INDUSTRY   / 100f) +
            (buildTypeScore  * WEIGHT_BUILD_TYPE  / 100f) +
            (problemScore    * WEIGHT_PROBLEMS    / 100f) +
            (featureScore    * WEIGHT_FEATURES    / 100f)
        );

        // Cap at 100
        total = Math.min(total, 100);

        String reason = buildReason(
            f, req, industryScore, buildTypeScore,
            problemScore, featureScore);

        return ScoringResult.builder()
                .foundation(f)
                .score(total)
                .reason(reason)
                .build();
    }

    private int scoreIndustry(Foundation f, String industry) {
        if (f.getIndustry().equalsIgnoreCase(industry)) {
            return 100;
        }
        boolean related = f.getRelatedIndustries().stream()
                .anyMatch(r -> r.equalsIgnoreCase(industry));
        return related ? 50 : 0;
    }

    private int scoreBuildType(Foundation f, String buildType) {
        if (buildType == null || buildType.isBlank()) return 50;
        boolean match = f.getBuildTypes().stream()
                .anyMatch(b -> b.equalsIgnoreCase(buildType));
        return match ? 100 : 20;
    }

    private int scoreList(List<String> foundationItems,
                          List<String> requestItems) {
        if (requestItems == null || requestItems.isEmpty()) return 0;
        if (foundationItems == null || foundationItems.isEmpty()) return 0;

        long matched = requestItems.stream()
                .filter(item -> foundationItems.stream()
                        .anyMatch(fi -> fi.equalsIgnoreCase(item)))
                .count();

        return (int) Math.round(
            (matched * 100.0) / requestItems.size());
    }

    private String buildReason(Foundation f, LeadRequest req,
                               int industryScore, int buildTypeScore,
                               int problemScore, int featureScore) {

        List<String> reasons = new ArrayList<>();

        if (industryScore == 100) {
            reasons.add(f.getName() + " is purpose-built for " +
                req.getIndustry());
        } else if (industryScore == 50) {
            reasons.add(f.getName() + " covers related workflows " +
                "in your industry");
        }

        if (buildTypeScore == 100) {
            reasons.add("matches your " + req.getBuildType() +
                " requirement");
        }

        // Matched problems
        if (req.getProblems() != null) {
            List<String> matchedProblems = req.getProblems().stream()
                    .filter(p -> f.getProblems().stream()
                            .anyMatch(fp -> fp.equalsIgnoreCase(p)))
                    .collect(Collectors.toList());
            if (!matchedProblems.isEmpty()) {
                reasons.add("solves: " +
                    String.join(", ", matchedProblems));
            }
        }

        // Matched features
        if (req.getFeatures() != null) {
            List<String> matchedFeatures = req.getFeatures().stream()
                    .filter(feat -> f.getFeatures().stream()
                            .anyMatch(ff -> ff.equalsIgnoreCase(feat)))
                    .collect(Collectors.toList());
            if (!matchedFeatures.isEmpty()) {
                reasons.add("includes: " +
                    String.join(", ", matchedFeatures));
            }
        }

        return reasons.isEmpty()
                ? f.getDescription()
                : String.join(". ", reasons) + ".";
    }
}