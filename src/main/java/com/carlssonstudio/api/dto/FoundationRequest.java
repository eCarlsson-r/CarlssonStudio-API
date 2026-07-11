package com.carlssonstudio.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class FoundationRequest {

    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9-]+$",
        message = "Slug must be lowercase letters, numbers, and hyphens")
    @Size(max = 50)
    private String slug;

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Industry is required")
    @Size(max = 50)
    private String industry;

    @NotNull @Size(min = 1)
    private List<String> relatedIndustries;

    @NotNull @Size(min = 1)
    private List<String> buildTypes;

    @NotNull @Size(min = 1)
    private List<String> problems;

    @NotNull @Size(min = 1)
    private List<String> features;

    private String description;
}