package com.carlssonstudio.api.service;

import com.carlssonstudio.api.config.MetaCapiProperties;
import com.carlssonstudio.api.dto.LeadResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MetaConversionsApiServiceTest {

    @Test
    void hashesEmailLowercasedAndTrimmed() {
        String hashed = MetaConversionsApiService.hashEmail("  Jane@RedVelvetBistro.com  ");
        // Same value hashed both ways must match — proves normalization
        // happens before hashing, not after.
        assertEquals(MetaConversionsApiService.hashEmail("jane@redvelvetbistro.com"), hashed);
        assertEquals(64, hashed.length()); // SHA-256 hex digest length
    }

    @Test
    void hashesPhoneUsingWhatsAppNormalization() {
        String hashed = MetaConversionsApiService.hashPhone("081234567890");
        assertNotNull(hashed);
        assertEquals(64, hashed.length());
        // "0812..." and "+62812..." normalize to the same digits, so
        // their hashes must match.
        assertEquals(hashed, MetaConversionsApiService.hashPhone("+62 812 3456 7890"));
    }

    @Test
    void returnsNullForBlankOrUnusableContactFields() {
        assertNull(MetaConversionsApiService.hashEmail(null));
        assertNull(MetaConversionsApiService.hashEmail(""));
        assertNull(MetaConversionsApiService.hashPhone(null));
        assertNull(MetaConversionsApiService.hashPhone("abc"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void buildsEventPayloadWithHashedUserDataAndCustomData() {
        MetaCapiProperties props = new MetaCapiProperties();
        props.setAccessToken("token");
        props.setPixelId("123456");
        props.setEventSourceUrl("https://carlssonstudio.com/start-a-project");
        MetaConversionsApiService service = new MetaConversionsApiService(props);

        LeadResponse lead = LeadResponse.builder()
                .id(1L).name("Jane").email("jane@redvelvetbistro.com")
                .phone("081234567890")
                .industry("Restaurant").buildType("POS")
                .build();

        Map<String, Object> payload = service.buildEventPayload(
            lead, "evt-123", "fb.1.111.222", "fb.1.111.333",
            "203.0.113.5", "Mozilla/5.0");

        assertEquals("token", payload.get("access_token"));
        List<Map<String, Object>> data = (List<Map<String, Object>>) payload.get("data");
        Map<String, Object> event = data.get(0);

        assertEquals("Lead", event.get("event_name"));
        assertEquals("evt-123", event.get("event_id"));
        assertEquals("website", event.get("action_source"));
        assertEquals("https://carlssonstudio.com/start-a-project", event.get("event_source_url"));

        Map<String, Object> userData = (Map<String, Object>) event.get("user_data");
        assertEquals(List.of(MetaConversionsApiService.hashEmail("jane@redvelvetbistro.com")), userData.get("em"));
        assertEquals(List.of(MetaConversionsApiService.hashPhone("081234567890")), userData.get("ph"));
        assertEquals("fb.1.111.222", userData.get("fbp"));
        assertEquals("fb.1.111.333", userData.get("fbc"));
        assertEquals("203.0.113.5", userData.get("client_ip_address"));
        assertEquals("Mozilla/5.0", userData.get("client_user_agent"));

        Map<String, Object> customData = (Map<String, Object>) event.get("custom_data");
        assertEquals("Restaurant", customData.get("industry"));
        assertEquals("POS", customData.get("build_type"));
    }

    @Test
    void generatesEventIdWhenNoneSupplied() {
        MetaCapiProperties props = new MetaCapiProperties();
        MetaConversionsApiService service = new MetaConversionsApiService(props);
        LeadResponse lead = LeadResponse.builder().id(1L).name("Jane").build();

        Map<String, Object> payload = service.buildEventPayload(
            lead, null, null, null, null, null);
        @SuppressWarnings("unchecked")
        Map<String, Object> event = ((List<Map<String, Object>>) payload.get("data")).get(0);

        assertNotNull(event.get("event_id"));
        assertFalse(((String) event.get("event_id")).isBlank());
    }

    @Test
    void skipsSilentlyWhenDisabledOrMisconfigured() {
        MetaCapiProperties props = new MetaCapiProperties();
        props.setEnabled(false);
        MetaConversionsApiService service = new MetaConversionsApiService(props);
        LeadResponse lead = LeadResponse.builder().id(1L).name("Jane").build();

        assertDoesNotThrow(() -> service.sendLeadEvent(lead, "e", null, null, null, null));

        props.setEnabled(true); // no pixelId/token set — still a no-op
        assertDoesNotThrow(() -> service.sendLeadEvent(lead, "e", null, null, null, null));
    }
}
