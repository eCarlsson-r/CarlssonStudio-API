package com.carlssonstudio.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises the full Spring-wired locale pipeline through the real public
 * controllers — LocaleResolver (Accept-Language) -> Bean Validation
 * message interpolation -> MessageSource -> ApiResponse envelope. Scoped
 * to validation failures so no request here ever reaches LeadService
 * (no DB write, no outbound email/Meta call).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LocalizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void leadValidationErrorsAreEnglishByDefault() throws Exception {
        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Name is required")));
    }

    @Test
    void leadValidationErrorsAreLocalizedForIndonesian() throws Exception {
        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "id")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Nama wajib diisi")));
    }

    @Test
    void contactRequiredCrossFieldMessageIsLocalized() throws Exception {
        String noContact = """
                {"name":"Budi","industry":"Restaurant","buildType":"POS",
                 "problems":["No reporting"],"features":["Reports"]}
                """;

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "id")
                        .content(noContact))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",
                        containsString("Isi alamat email atau nomor WhatsApp")));
    }

    @Test
    void questionnaireEnvelopeMessageIsLocalized() throws Exception {
        mockMvc.perform(get("/api/config/questionnaire")
                        .header("Accept-Language", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Konfigurasi kuesioner"));
    }

    @Test
    void questionnaireEnvelopeMessageDefaultsToEnglish() throws Exception {
        mockMvc.perform(get("/api/config/questionnaire"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Questionnaire config"));
    }

    @Test
    void unsupportedLocaleFallsBackToEnglish() throws Exception {
        // "fr" isn't in LocaleConfig's supported list — should resolve to
        // the configured default (English), not the JVM's system locale.
        mockMvc.perform(get("/api/config/questionnaire")
                        .header("Accept-Language", "fr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Questionnaire config"));
    }
}
