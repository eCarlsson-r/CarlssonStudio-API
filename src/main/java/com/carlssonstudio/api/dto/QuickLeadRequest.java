package com.carlssonstudio.api.dto;

import com.carlssonstudio.api.entity.BusinessStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuickLeadRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Industry is required")
    private String industry;

    @NotNull
    private BusinessStatus businessStatus; // enum: RUNNING, PLANNING

    private String goal; // nullable, no validation — only meaningful when PLANNING
}