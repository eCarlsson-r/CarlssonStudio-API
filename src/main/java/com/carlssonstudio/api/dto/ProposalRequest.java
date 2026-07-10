package com.carlssonstudio.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProposalRequest {
    @NotNull(message = "Lead ID is required")
    private Long leadId;

    @NotBlank(message = "Foundation slug is required")
    private String foundationSlug;
}