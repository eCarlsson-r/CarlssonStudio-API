package com.carlssonstudio.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalLeads;
    private long leadsThisWeek;
    private long leadsThisMonth;
    private Map<String, Long> leadsByStatus;
    private Map<String, Long> leadsByIndustry;
    private Map<String, Long> leadsBySource;
    private long totalProposals;
    private Map<String, Long> proposalsByStatus;
}