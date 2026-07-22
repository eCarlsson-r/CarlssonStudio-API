package com.carlssonstudio.api.service;

import com.carlssonstudio.api.config.WhatsAppProperties;
import com.carlssonstudio.api.dto.LeadResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WhatsAppServiceTest {

    @Test
    void normalizesIndonesianLocalNumbers() {
        assertEquals("6281234567890",
            WhatsAppService.normalizeToWhatsAppNumber("081234567890"));
        assertEquals("6281234567890",
            WhatsAppService.normalizeToWhatsAppNumber("0812-3456-7890"));
    }

    @Test
    void keepsInternationalNumbersAndStripsFormatting() {
        assertEquals("6281234567890",
            WhatsAppService.normalizeToWhatsAppNumber("+62 812 3456 7890"));
        assertEquals("14155552671",
            WhatsAppService.normalizeToWhatsAppNumber("+1 (415) 555-2671"));
    }

    @Test
    void rejectsUnusableNumbers() {
        assertNull(WhatsAppService.normalizeToWhatsAppNumber(null));
        assertNull(WhatsAppService.normalizeToWhatsAppNumber("   "));
        assertNull(WhatsAppService.normalizeToWhatsAppNumber("abc"));
        assertNull(WhatsAppService.normalizeToWhatsAppNumber("12345"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void buildsTemplatePayloadWithThreeBodyParameters() {
        WhatsAppProperties props = new WhatsAppProperties();
        props.setTemplateName("lead_followup");
        props.setTemplateLanguage("en");
        WhatsAppService service = new WhatsAppService(props);

        Map<String, Object> payload = service.buildTemplatePayload(
            "6281234567890", "Jane", "RestoSystem", "100");

        assertEquals("whatsapp", payload.get("messaging_product"));
        assertEquals("6281234567890", payload.get("to"));
        assertEquals("template", payload.get("type"));

        Map<String, Object> template =
            (Map<String, Object>) payload.get("template");
        assertEquals("lead_followup", template.get("name"));

        List<Map<String, Object>> components =
            (List<Map<String, Object>>) template.get("components");
        List<Map<String, Object>> params =
            (List<Map<String, Object>>) components.get(0)
                .get("parameters");
        assertEquals(3, params.size());
        assertEquals("Jane", params.get(0).get("text"));
        assertEquals("RestoSystem", params.get(1).get("text"));
        assertEquals("100", params.get(2).get("text"));
    }

    @Test
    void skipsSilentlyWhenDisabledOrNotOptedIn() {
        WhatsAppProperties props = new WhatsAppProperties();
        props.setEnabled(false);
        WhatsAppService service = new WhatsAppService(props);

        LeadResponse lead = LeadResponse.builder()
                .id(1L).name("Test").email("t@t.com")
                .phone("081234567890").whatsappOptIn(true)
                .build();

        // Disabled — must be a no-op, no exception
        assertDoesNotThrow(() -> service.sendLeadFollowUp(lead));

        // Enabled but prospect did not opt in — also a no-op
        props.setEnabled(true);
        props.setToken("token");
        props.setPhoneNumberId("12345");
        LeadResponse noOptIn = LeadResponse.builder()
                .id(2L).name("Test").email("t@t.com")
                .phone("081234567890").whatsappOptIn(false)
                .build();
        assertDoesNotThrow(() -> service.sendLeadFollowUp(noOptIn));

        // Enabled + opted in but no phone — no-op
        LeadResponse noPhone = LeadResponse.builder()
                .id(3L).name("Test").email("t@t.com")
                .whatsappOptIn(true)
                .build();
        assertDoesNotThrow(() -> service.sendLeadFollowUp(noPhone));
    }
}
