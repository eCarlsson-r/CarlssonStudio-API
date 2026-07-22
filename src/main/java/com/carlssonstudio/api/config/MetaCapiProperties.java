package com.carlssonstudio.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.meta")
public class MetaCapiProperties {
    /** Master switch — nothing is sent while false. */
    private boolean enabled = false;
    /** Meta Pixel ID (same one loaded client-side). */
    private String pixelId;
    /** System-user access token with ads_management permission. */
    private String accessToken;
    /** Graph API version segment. */
    private String apiVersion = "v20.0";
    /** Public URL of the page the Lead event is attributed to. */
    private String eventSourceUrl = "https://carlssonstudio.com/start-a-project";
    /** Meta Test Events code (Events Manager → Test Events) — leave
     *  blank in production, set temporarily while verifying setup. */
    private String testEventCode;
}
