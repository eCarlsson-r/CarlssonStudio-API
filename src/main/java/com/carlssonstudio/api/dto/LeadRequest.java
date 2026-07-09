package com.carlssonstudio.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class LeadRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150)
    private String email;

    @Size(max = 100)
    private String company;

    private String companySize;  // "1-5", "5-20", "20-100", "100+"

    @NotBlank(message = "Industry is required")
    private String industry;

    @NotBlank(message = "Build type is required")
    private String buildType;

    @NotNull(message = "Problems list is required")
    @Size(min = 1, message = "Select at least one problem")
    private List<String> problems;

    @NotNull(message = "Features list is required")
    @Size(min = 1, message = "Select at least one feature")
    private List<String> features;
}