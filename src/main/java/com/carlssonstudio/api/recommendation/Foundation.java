package com.carlssonstudio.api.recommendation;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class Foundation {
    private String slug;
    private String name;
    private String industry;
    private List<String> relatedIndustries;
    private List<String> buildTypes;
    private List<String> problems;
    private List<String> features;
    private String description;
}