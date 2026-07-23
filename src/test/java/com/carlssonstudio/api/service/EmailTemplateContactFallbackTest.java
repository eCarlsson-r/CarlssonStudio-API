package com.carlssonstudio.api.service;

import com.carlssonstudio.api.dto.LeadResponse;
import com.carlssonstudio.api.dto.RecommendationResponse;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A lead may now supply only a phone number (LeadRequest#isContactProvided
 * accepts email OR phone). The notification email must still render a
 * working "Reply to" button by falling back to a wa.me link.
 */
class EmailTemplateContactFallbackTest {

    private final EmailTemplateService service = new EmailTemplateService();

    private LeadResponse.LeadResponseBuilder baseLead() {
        return LeadResponse.builder()
            .id(1L)
            .name("Jane Doe")
            .industry("Restaurant")
            .buildType("POS")
            .problems(List.of("Manual spreadsheets"))
            .features(List.of("Reports"))
            .recommendations(List.of(
                RecommendationResponse.builder()
                    .foundationSlug("resto-system")
                    .foundationName("RestoSystem")
                    .matchScore(100)
                    .matchReason("Great match.")
                    .build()));
    }

    @Test
    void whatsappOnlyLeadGetsWaMeReplyButton() {
        LeadResponse lead = baseLead()
            .email(null)
            .phone("081234567890")
            .build();

        String html = service.buildLeadNotificationHtml(lead);

        assertTrue(html.contains("https://wa.me/6281234567890"),
            "Expected a wa.me reply link when no email is present");
        assertFalse(html.contains("mailto:"),
            "Should not render a mailto link when email is absent");
    }

    @Test
    void emailOnlyLeadStillGetsMailtoReplyButton() {
        LeadResponse lead = baseLead()
            .email("jane@redvelvetbistro.com")
            .phone(null)
            .build();

        String html = service.buildLeadNotificationHtml(lead);

        assertTrue(html.contains("mailto:jane@redvelvetbistro.com"));
    }

    @Test
    void emailRowShowsPlaceholderWhenAbsent() {
        LeadResponse lead = baseLead()
            .email(null)
            .phone("081234567890")
            .build();

        String html = service.buildLeadNotificationHtml(lead);

        assertTrue(html.contains(">&mdash;<") || html.contains(">—<"),
            "Expected an em dash placeholder for the missing email field");
    }
}
