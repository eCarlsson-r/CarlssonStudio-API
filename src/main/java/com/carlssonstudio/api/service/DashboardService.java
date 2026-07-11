package com.carlssonstudio.api.service;

import com.carlssonstudio.api.dto.*;
import com.carlssonstudio.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LeadRepository leadRepository;
    private final ProposalRepository proposalRepository;
    private final LeadRecommendationRepository
        recommendationRepository;
    private final FoundationRepository foundationRepository;

    public DashboardStatsResponse stats() {
        LocalDateTime weekAgo =
            LocalDateTime.now().minusWeeks(1);
        LocalDateTime monthAgo =
            LocalDateTime.now().minusMonths(1);

        return DashboardStatsResponse.builder()
                .totalLeads(leadRepository.count())
                .leadsThisWeek(
                    leadRepository.countByCreatedAtAfter(weekAgo))
                .leadsThisMonth(
                    leadRepository.countByCreatedAtAfter(monthAgo))
                .leadsByStatus(toMap(
                    leadRepository.countGroupByStatus()))
                .leadsByIndustry(toMap(
                    leadRepository.countGroupByIndustry()))
                .totalProposals(proposalRepository.count())
                .proposalsByStatus(toMap(
                    proposalRepository.countGroupByStatus()))
                .build();
    }

    public List<FoundationPopularityResponse> foundationPopularity() {
        Map<String, Long> topMatches =
            recommendationRepository.topMatchCounts()
                .stream()
                .collect(Collectors.toMap(
                    row -> (String) row[0],
                    row -> (Long) row[1]));

        return recommendationRepository.foundationPopularity()
                .stream()
                .map(row -> {
                    String slug = (String) row[0];
                    return FoundationPopularityResponse.builder()
                        .foundationSlug(slug)
                        .foundationName(resolveName(slug))
                        .timesRecommended((Long) row[1])
                        .timesTopMatch(
                            topMatches.getOrDefault(slug, 0L))
                        .avgMatchScore(
                            Math.round(((Double) row[2]) * 10)
                                / 10.0)
                        .build();
                })
                .collect(Collectors.toList());
    }

    public List<TimelinePointResponse> timeline(int days) {
        LocalDateTime since =
            LocalDateTime.now().minusDays(days);

        Map<LocalDate, Long> counts =
            leadRepository.countPerDaySince(since)
                .stream()
                .collect(Collectors.toMap(
                    row -> ((java.sql.Date) row[0])
                        .toLocalDate(),
                    row -> (Long) row[1]));

        // Fill gaps with zeros
        List<TimelinePointResponse> result =
            new ArrayList<>();
        LocalDate cursor = LocalDate.now()
            .minusDays(days - 1);
        LocalDate today = LocalDate.now();

        while (!cursor.isAfter(today)) {
            result.add(TimelinePointResponse.builder()
                .date(cursor)
                .count(counts.getOrDefault(cursor, 0L))
                .build());
            cursor = cursor.plusDays(1);
        }
        return result;
    }

    private Map<String, Long> toMap(List<Object[]> rows) {
        return rows.stream()
                .collect(Collectors.toMap(
                    row -> row[0].toString(),
                    row -> (Long) row[1],
                    (a, b) -> a,
                    LinkedHashMap::new));
    }

    private String resolveName(String slug) {
        return foundationRepository.findBySlug(slug)
                .map(f -> f.getName())
                .orElse(slug);
    }
}