package com.carlssonstudio.api.recommendation;

import com.carlssonstudio.api.dto.LeadRequest;
import com.carlssonstudio.api.entity.FoundationEntity;
import com.carlssonstudio.api.repository.FoundationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationEngine {

    private final FoundationRepository foundationRepository;
    private final MessageSource messageSource;

    // Weights — must sum to 100
    private static final int WEIGHT_INDUSTRY   = 40;
    private static final int WEIGHT_BUILD_TYPE = 25;
    private static final int WEIGHT_PROBLEMS   = 20;
    private static final int WEIGHT_FEATURES   = 15;

    public List<ScoringResult> recommend(LeadRequest request, Locale locale) {
    	return foundationRepository.findByActiveTrue()
                .stream()
                .map(this::toFoundation)
                .map(f -> score(f, request, locale))
                .sorted(Comparator
                    .comparingInt(ScoringResult::getScore)
                    .reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    private Foundation toFoundation(FoundationEntity e) {
        return Foundation.builder()
                .slug(e.getSlug())
                .name(e.getName())
                .industry(e.getIndustry())
                .relatedIndustries(e.getRelatedIndustries())
                .buildTypes(e.getBuildTypes())
                .problems(e.getProblems())
                .features(e.getFeatures())
                .description(e.getDescription())
                .build();
    }

    private ScoringResult score(Foundation f, LeadRequest req, Locale locale) {
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
            f, req, locale, industryScore, buildTypeScore,
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

    /**
     * Composes the human-readable reason in the caller's locale. Sentence
     * templates and connective phrases come from MessageSource
     * (messages.properties / messages_id.properties); the entity values
     * embedded in them (industry, build type, problem and feature names)
     * are translated separately via OptionLabels, since those are open,
     * free-text catalog values rather than fixed system messages.
     */
    private String buildReason(Foundation f, LeadRequest req, Locale locale,
                               int industryScore, int buildTypeScore,
                               int problemScore, int featureScore) {

        List<String> reasons = new ArrayList<>();

        if (industryScore == 100) {
            String industryLabel = OptionLabels.industry(req.getIndustry(), locale);
            reasons.add(messageSource.getMessage(
                "reason.industry.exact",
                new Object[]{f.getName(), industryLabel}, locale));
        } else if (industryScore == 50) {
            reasons.add(messageSource.getMessage(
                "reason.industry.related",
                new Object[]{f.getName()}, locale));
        }

        if (buildTypeScore == 100) {
            String buildTypeLabel = OptionLabels.buildType(req.getBuildType(), locale);
            reasons.add(messageSource.getMessage(
                "reason.buildType.match",
                new Object[]{buildTypeLabel}, locale));
        }

        // Matched problems
        if (req.getProblems() != null) {
            List<String> matchedProblems = req.getProblems().stream()
                    .filter(p -> f.getProblems().stream()
                            .anyMatch(fp -> fp.equalsIgnoreCase(p)))
                    .map(p -> OptionLabels.problem(p, locale))
                    .collect(Collectors.toList());
            if (!matchedProblems.isEmpty()) {
                reasons.add(messageSource.getMessage(
                    "reason.problems.solves",
                    new Object[]{String.join(", ", matchedProblems)}, locale));
            }
        }

        // Matched features
        if (req.getFeatures() != null) {
            List<String> matchedFeatures = req.getFeatures().stream()
                    .filter(feat -> f.getFeatures().stream()
                            .anyMatch(ff -> ff.equalsIgnoreCase(feat)))
                    .map(feat -> OptionLabels.feature(feat, locale))
                    .collect(Collectors.toList());
            if (!matchedFeatures.isEmpty()) {
                reasons.add(messageSource.getMessage(
                    "reason.features.includes",
                    new Object[]{String.join(", ", matchedFeatures)}, locale));
            }
        }

        // Fallback is the foundation's DB-authored description, English
        // only for now — see the summary note on a description_id column.
        return reasons.isEmpty()
                ? f.getDescription()
                : String.join(". ", reasons) + ".";
    }
}
