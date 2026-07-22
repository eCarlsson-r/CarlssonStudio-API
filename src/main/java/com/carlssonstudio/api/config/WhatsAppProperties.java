package com.carlssonstudio.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.whatsapp")
public class WhatsAppProperties {
    /** Master switch — nothing is sent while false. */
    private boolean enabled = false;
    /** Permanent system-user access token with whatsapp_business_messaging. */
    private String token;
    /** Phone number ID from the WABA (not the phone number itself). */
    private String phoneNumberId;
    /** Pre-approved template name in Meta Business Manager. */
    private String templateName = "lead_followup";
    /** Language code the template was approved for. */
    private String templateLanguage = "en";
    /** Graph API version segment. */
    private String apiVersion = "v20.0";
}
