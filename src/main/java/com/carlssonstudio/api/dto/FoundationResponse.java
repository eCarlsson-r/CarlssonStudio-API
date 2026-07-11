package com.carlssonstudio.api.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FoundationResponse {
    private Long id;
    private String slug;
    private String name;
    private String industry;
    private List<String> relatedIndustries;
    private List<String> buildTypes;
    private List<String> problems;
    private List<String> features;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}