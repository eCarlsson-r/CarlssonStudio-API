package com.carlssonstudio.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class LeadRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @Email(message = "Invalid email format")
    @Size(max = 150)
    private String email;

    @Size(max = 25)
    @Pattern(regexp = "^[+0-9 ().-]*$",
             message = "Invalid phone number format")
    private String phone;

    private Boolean whatsappOptIn;

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

    // Meta ad-tracking metadata — all optional, absent when the Pixel
    // is blocked or the visitor didn't arrive from an ad. fbEventId ties
    // this submission's server-side Conversions API event to the
    // matching client-side Pixel event so Meta deduplicates the pair.
    private String fbEventId;
    private String fbp;
    private String fbc;

    /**
     * Cross-field rule: a prospect must be reachable somehow. Bean
     * Validation runs this as a property named "contactProvided" —
     * reported as a normal field error by GlobalExceptionHandler.
     */
    @AssertTrue(message = "Provide an email address or a WhatsApp number")
    public boolean isContactProvided() {
        return (email != null && !email.isBlank())
            || (phone != null && !phone.isBlank());
    }
}