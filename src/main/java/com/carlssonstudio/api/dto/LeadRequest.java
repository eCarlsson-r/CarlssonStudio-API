package com.carlssonstudio.api.dto;

import com.carlssonstudio.api.entity.BusinessStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class LeadRequest {

    @NotBlank(message = "{lead.name.required}")
    @Size(max = 100)
    private String name;

    @Email(message = "{lead.email.invalid}")
    @Size(max = 150)
    private String email;

    @Size(max = 25)
    @Pattern(regexp = "^[+0-9 ().-]*$",
             message = "{lead.phone.invalid}")
    private String phone;

    @Size(max = 100)
    private String company;

    private String companySize;  // "1-5", "5-20", "20-100", "100+"

    @NotBlank(message = "{lead.industry.required}")
    private String industry;

    @NotBlank(message = "{lead.buildType.required}")
    private String buildType;

    /**
     * Whether the business is already operating or still being planned —
     * the same signal Quick Match captures (see QuickLeadRequest). Left
     * optional rather than @NotNull so a cached older frontend bundle
     * degrades to RUNNING (see LeadService#submit) instead of getting a 400.
     */
    private BusinessStatus businessStatus;

    /** Free-text objective; only meaningful when businessStatus is PLANNING. */
    private String goal;

    @NotNull(message = "{lead.problems.required}")
    @Size(min = 1, message = "{lead.problems.required}")
    private List<String> problems;

    @NotNull(message = "{lead.features.required}")
    @Size(min = 1, message = "{lead.features.required}")
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
    @AssertTrue(message = "{lead.contact.required}")
    public boolean isContactProvided() {
        return (email != null && !email.isBlank())
            || (phone != null && !phone.isBlank());
    }
}