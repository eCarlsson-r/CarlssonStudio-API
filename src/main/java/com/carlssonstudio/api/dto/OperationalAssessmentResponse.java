package com.carlssonstudio.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class OperationalAssessmentResponse {
    private int score;
    private String summary;
    private List<AssessmentFindingResponse> findings;
    private List<AssessmentPriorityResponse> priorities;
    private List<String> recommendationBenefits;
}