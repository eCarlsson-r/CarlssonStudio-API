package com.carlssonstudio.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssessmentFindingResponse {
    private String title;
    private String description;
}