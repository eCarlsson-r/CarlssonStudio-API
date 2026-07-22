package com.carlssonstudio.api.service;

import com.carlssonstudio.api.config.MetaCapiProperties;
import com.carlssonstudio.api.dto.LeadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Sends the "Lead" event to Meta's Conversions API (server-side) the
 * moment a lead is actually saved — the reliable counterpart to the
 * client-side Pixel, which Safari/iOS and ad blockers increasingly clip.
 *
 * The frontend fires the same standard event with the same event_id via
 * the Pixel; Meta deduplicates any event pair that shares one, so this
 * is additive coverage, not a duplicate count.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetaConversionsApiService {

    private final MetaCapiProperties properties;

    @Async
    public void sendLeadEvent(LeadResponse lead, String eventId,
                              String fbp, String fbc,
                              String clientIp, String userAgent) {
        try {
            if (!properties.isEnabled()) {
                return;
            }
            if (isBlank(properties.getPixelId())
                    || isBlank(properties.getAccessToken())) {
                log.warn("Meta CAPI enabled but pixelId/accessToken "
                    + "missing — skipping send for lead id={}",
                    lead.getId());
                return;
            }

            Map<String, Object> payload = buildEventPayload(
                lead, eventId, fbp, fbc, clientIp, userAgent);

            String url = "https://graph.facebook.com/"
                + properties.getApiVersion() + "/"
                + properties.getPixelId() + "/events";

            RestClient.create().post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

            log.info("Meta CAPI Lead event sent for lead id={}",
                lead.getId());

        } catch (Exception e) {
            // Never fail the main flow due to a tracking error
            log.error("Failed to send Meta CAPI event for "
                + "lead id={}: {}", lead.getId(), e.getMessage());
        }
    }

    Map<String, Object> buildEventPayload(LeadResponse lead,
                                          String eventId,
                                          String fbp, String fbc,
                                          String clientIp,
                                          String userAgent) {
        Map<String, Object> userData = new LinkedHashMap<>();
        String hashedEmail = hashEmail(lead.getEmail());
        String hashedPhone = hashPhone(lead.getPhone());
        if (hashedEmail != null) userData.put("em", List.of(hashedEmail));
        if (hashedPhone != null) userData.put("ph", List.of(hashedPhone));
        if (!isBlank(fbp)) userData.put("fbp", fbp);
        if (!isBlank(fbc)) userData.put("fbc", fbc);
        if (!isBlank(clientIp)) userData.put("client_ip_address", clientIp);
        if (!isBlank(userAgent)) userData.put("client_user_agent", userAgent);

        Map<String, Object> customData = new LinkedHashMap<>();
        if (lead.getIndustry() != null) customData.put("industry", lead.getIndustry());
        if (lead.getBuildType() != null) customData.put("build_type", lead.getBuildType());

        Map<String, Object> event = new LinkedHashMap<>();
        event.put("event_name", "Lead");
        event.put("event_time", Instant.now().getEpochSecond());
        event.put("event_id", isBlank(eventId) ? UUID.randomUUID().toString() : eventId);
        event.put("action_source", "website");
        event.put("event_source_url", properties.getEventSourceUrl());
        event.put("user_data", userData);
        event.put("custom_data", customData);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("data", List.of(event));
        payload.put("access_token", properties.getAccessToken());
        if (!isBlank(properties.getTestEventCode())) {
            payload.put("test_event_code", properties.getTestEventCode());
        }
        return payload;
    }

    /** Lowercase + trim, then SHA-256 hex — Meta's required email format. */
    static String hashEmail(String email) {
        if (email == null || email.isBlank()) return null;
        return sha256Hex(email.trim().toLowerCase());
    }

    /** Digits-only international format (reuses the WhatsApp normalizer),
     *  then SHA-256 hex — Meta's required phone format. */
    static String hashPhone(String phone) {
        String normalized = WhatsAppService.normalizeToWhatsAppNumber(phone);
        return normalized == null ? null : sha256Hex(normalized);
    }

    private static String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
