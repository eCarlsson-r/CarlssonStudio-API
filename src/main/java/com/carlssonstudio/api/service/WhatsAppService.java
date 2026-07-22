package com.carlssonstudio.api.service;

import com.carlssonstudio.api.config.WhatsAppProperties;
import com.carlssonstudio.api.dto.LeadResponse;
import com.carlssonstudio.api.dto.RecommendationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Sends the post-submission follow-up via the official WhatsApp Business
 * Cloud API (Meta Graph API). Business-initiated messages must use a
 * pre-approved template; this service fills the template's body
 * parameters with {{1}} name, {{2}} top foundation, {{3}} match score.
 *
 * Sending is best-effort: it only happens when the feature is enabled,
 * the prospect supplied a number AND opted in, and any failure is logged
 * without ever affecting lead submission.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppService {

    private final WhatsAppProperties properties;

    @Async
    public void sendLeadFollowUp(LeadResponse lead) {
        try {
            if (!properties.isEnabled()) {
                return;
            }
            if (isBlank(properties.getToken())
                    || isBlank(properties.getPhoneNumberId())) {
                log.warn("WhatsApp enabled but token/phoneNumberId "
                    + "missing — skipping send for lead id={}",
                    lead.getId());
                return;
            }
            if (!lead.isWhatsappOptIn() || isBlank(lead.getPhone())) {
                return;
            }
            RecommendationResponse top = topRecommendation(lead);
            if (top == null) {
                log.warn("Lead id={} has no recommendations — "
                    + "skipping WhatsApp follow-up", lead.getId());
                return;
            }

            String to = normalizeToWhatsAppNumber(lead.getPhone());
            if (to == null) {
                log.warn("Lead id={} phone '{}' could not be "
                    + "normalized — skipping WhatsApp follow-up",
                    lead.getId(), lead.getPhone());
                return;
            }

            Map<String, Object> payload = buildTemplatePayload(
                to, lead.getName(),
                top.getFoundationName(),
                String.valueOf(top.getMatchScore()));

            String url = "https://graph.facebook.com/"
                + properties.getApiVersion() + "/"
                + properties.getPhoneNumberId() + "/messages";

            RestClient.create().post()
                .uri(url)
                .header("Authorization",
                    "Bearer " + properties.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

            log.info("WhatsApp follow-up sent for lead id={}",
                lead.getId());

        } catch (Exception e) {
            // Never fail the main flow due to WhatsApp errors
            log.error("Failed to send WhatsApp follow-up for "
                + "lead id={}: {}", lead.getId(), e.getMessage());
        }
    }

    /**
     * Cloud API expects digits only, in international format without
     * a leading '+'. A leading '0' is treated as an Indonesian local
     * number and rewritten to country code 62.
     */
    static String normalizeToWhatsAppNumber(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return null;
        if (digits.startsWith("0")) {
            digits = "62" + digits.substring(1);
        }
        // Anything shorter than 8 digits cannot be a routable number
        return digits.length() < 8 ? null : digits;
    }

    Map<String, Object> buildTemplatePayload(String to, String name,
                                             String foundationName,
                                             String score) {
        return Map.of(
            "messaging_product", "whatsapp",
            "to", to,
            "type", "template",
            "template", Map.of(
                "name", properties.getTemplateName(),
                "language", Map.of(
                    "code", properties.getTemplateLanguage()),
                "components", List.of(Map.of(
                    "type", "body",
                    "parameters", List.of(
                        Map.of("type", "text", "text", name),
                        Map.of("type", "text", "text", foundationName),
                        Map.of("type", "text", "text", score))))));
    }

    private RecommendationResponse topRecommendation(LeadResponse lead) {
        List<RecommendationResponse> recs = lead.getRecommendations();
        return (recs == null || recs.isEmpty()) ? null : recs.get(0);
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
