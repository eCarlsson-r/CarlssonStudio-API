package com.carlssonstudio.api.service;

import com.carlssonstudio.api.dto.LeadResponse;
import com.carlssonstudio.api.dto.RecommendationResponse;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Renders the lead notification email with sample data to target/email-preview.html. */
class EmailTemplatePreviewTest {

    @Test
    void renderPreview() throws Exception {
        LeadResponse lead = LeadResponse.builder()
            .id(42L)
            .name("Jane Doe")
            .email("jane@redvelvetbistro.com")
            .company("Red Velvet Bistro")
            .companySize("5-20")
            .industry("Restaurant")
            .buildType("POS")
            .problems(List.of("Manual spreadsheets", "No reporting", "No inventory tracking"))
            .features(List.of("Payments", "Reports", "Inventory", "Dashboard"))
            .recommendations(List.of(
                RecommendationResponse.builder()
                    .foundationSlug("resto-system")
                    .foundationName("RestoSystem")
                    .matchScore(100)
                    .matchReason("RestoSystem is purpose-built for restaurant operations: POS, reservations, kitchen workflow, and analytics in one platform.")
                    .build()))
            .build();

        String html = new EmailTemplateService().buildLeadNotificationHtml(lead);
        Files.writeString(Path.of("target/email-preview.html"), html);
    }
}
