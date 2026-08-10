package com.carlssonstudio.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssessmentPriorityResponse {
    private String title;
    private String description;
    private String icon;
}