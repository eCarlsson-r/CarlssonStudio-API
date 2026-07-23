package com.carlssonstudio.api.service;

import com.carlssonstudio.api.dto.LeadResponse;
import com.carlssonstudio.api.dto.RecommendationResponse;
import com.carlssonstudio.api.util.PhoneNumberUtils;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Builds HTML emails matching the carlssonstudio.com brand:
 * teal primary (#146C7C), burnt-sienna accent (#9A4D1F), serif headlines,
 * and the nine-foundation ribbon. Layout is table-based with inline styles
 * so it renders correctly in Gmail and Outlook (no flexbox, no external CSS).
 */
@Service
public class EmailTemplateService {

    /** One segment per foundation — mirrors ribbonAccents in the frontend. */
    private static final String[] RIBBON = {
        "#9A4D1F", "#94221F", "#1D64AD", "#1B7BB3", "#2B3A96",
        "#147A5F", "#4A37A8", "#2F6E75", "#D4AF37"
    };

    private static final String SERIF = "Georgia, 'Times New Roman', serif";
    private static final String SANS = "Arial, Helvetica, sans-serif";

    private static final String TEAL = "#146C7C";
    private static final String TEAL_DARK = "#0D4A45";
    private static final String TEAL_TINT = "#E3F1F0";
    private static final String SIENNA = "#9A4D1F";
    private static final String INK = "#1A1C1C";
    private static final String MUTED = "#5D6F73";
    private static final String SURFACE = "#F7F9F9";
    private static final String BORDER = "#E5E9E9";

    public String buildLeadNotificationHtml(LeadResponse lead) {
        RecommendationResponse top = lead.getRecommendations() == null
                || lead.getRecommendations().isEmpty()
                ? null : lead.getRecommendations().get(0);

        String name = escape(lead.getName());
        String email = escape(nullSafe(lead.getEmail(), "—"));

        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>New Lead — Carlsson Studio</title>
            </head>
            <body style="margin:0; padding:0; background-color:%s;">
              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:%s;">
                <tr>
                  <td align="center" style="padding:32px 16px;">
                    <table role="presentation" width="600" cellpadding="0" cellspacing="0"
                           style="width:100%%; max-width:600px; background-color:#ffffff; border:1px solid %s; border-radius:12px; overflow:hidden;">

                      <!-- Header -->
                      <tr>
                        <td style="padding:28px 32px 20px;">
                          <span style="font-family:%s; font-size:24px; font-weight:bold; color:%s;">Carlsson Studio</span>
                          <br>
                          <span style="font-family:%s; font-size:12px; letter-spacing:2px; text-transform:uppercase; color:%s;">New project inquiry</span>
                        </td>
                      </tr>

                      <!-- Foundation ribbon -->
                      <tr>
                        <td>
                          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"><tr>%s</tr></table>
                        </td>
                      </tr>

                      <!-- Body -->
                      <tr>
                        <td style="padding:28px 32px 8px;">
                          <span style="display:inline-block; background-color:%s; color:#ffffff; font-family:%s; font-size:11px; font-weight:bold; letter-spacing:1px; padding:5px 14px; border-radius:4px;">NEW LEAD</span>
                        </td>
                      </tr>

                      <tr>
                        <td style="padding:20px 32px 0;">
                          %s
                          %s
                        </td>
                      </tr>

                      <!-- Problems / Features -->
                      <tr>
                        <td style="padding:20px 32px 0;">
                          %s
                          %s
                        </td>
                      </tr>

                      <!-- Top match -->
                      %s

                      <!-- CTA -->
                      %s

                      <!-- Footer -->
                      <tr>
                        <td align="center" style="padding:20px 32px 26px; border-top:1px solid %s;">
                          <span style="font-family:%s; font-size:12px; color:%s;">&copy; Carlsson Studio &middot; Custom Business Software</span>
                          <br>
                          <span style="font-family:%s; font-size:12px; color:%s;">Independent Software Studio based in Medan, Indonesia</span>
                        </td>
                      </tr>

                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(
                SURFACE, SURFACE, BORDER,
                SERIF, TEAL,
                SANS, MUTED,
                ribbonHtml(),
                SIENNA, SANS,
                sectionHtml("Contact details", List.of(
                    field("Name", name),
                    field("Email", email),
                    field("WhatsApp", whatsappFieldHtml(lead)),
                    field("Company", escape(nullSafe(lead.getCompany(), "—"))),
                    field("Company size", escape(nullSafe(lead.getCompanySize(), "—")))
                )),
                sectionHtml("Project details", List.of(
                    field("Industry", escape(lead.getIndustry())),
                    field("Build type", escape(lead.getBuildType()))
                )),
                tagsSectionHtml("Problems to solve", lead.getProblems()),
                tagsSectionHtml("Required features", lead.getFeatures()),
                top != null ? recommendationHtml(top) : "",
                replyCtaHtml(lead, name),
                BORDER, SANS, MUTED, SANS, MUTED
            );
    }

    /**
     * "Reply to {name}" opens email when one was given; otherwise it
     * opens a WhatsApp chat, since LeadRequest guarantees at least one
     * of the two exists. Falls back to plain text in the (practically
     * unreachable) case neither is present, so the layout never breaks.
     */
    private String replyCtaHtml(LeadResponse lead, String name) {
        String href;
        if (lead.getEmail() != null && !lead.getEmail().isBlank()) {
            href = "mailto:" + escape(lead.getEmail());
        } else {
            String normalized = PhoneNumberUtils
                .normalizeToWhatsAppNumber(lead.getPhone());
            href = normalized != null ? "https://wa.me/" + normalized : null;
        }
        if (href == null) {
            return "";
        }
        return """
            <tr>
              <td align="center" style="padding:28px 32px;">
                <table role="presentation" cellpadding="0" cellspacing="0">
                  <tr>
                    <td style="background-color:%s; border-radius:8px;">
                      <a href="%s" style="display:inline-block; padding:13px 32px; font-family:%s; font-size:14px; font-weight:bold; color:#ffffff; text-decoration:none;">Reply to %s &rarr;</a>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            """.formatted(TEAL, href, SANS, name);
    }

    /** Tap-to-chat wa.me link so the notification email doubles as a
     *  one-click WhatsApp opener; falls back to a dash when the
     *  prospect left no number. */
    private String whatsappFieldHtml(LeadResponse lead) {
        String normalized = PhoneNumberUtils
            .normalizeToWhatsAppNumber(lead.getPhone());
        if (normalized == null) {
            return "—";
        }
        return "<a href=\"https://wa.me/" + normalized
            + "\" style=\"color:" + TEAL
            + "; text-decoration:none;\">+" + normalized + "</a>";
    }

    private String ribbonHtml() {
        StringBuilder sb = new StringBuilder();
        for (String color : RIBBON) {
            sb.append("<td height=\"4\" style=\"height:4px; font-size:0; line-height:0; background-color:")
              .append(color).append(";\">&nbsp;</td>");
        }
        return sb.toString();
    }

    private String sectionHtml(String title, List<String> rows) {
        return """
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:20px;">
              <tr><td style="font-family:%s; font-size:12px; letter-spacing:1.5px; text-transform:uppercase; color:%s; padding-bottom:8px;">%s</td></tr>
              %s
            </table>
            """.formatted(SANS, MUTED, title, String.join("\n", rows));
    }

    private String field(String label, String value) {
        return """
            <tr>
              <td style="padding:7px 0; border-bottom:1px solid #EDF1F1;">
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0">
                  <tr>
                    <td width="140" style="font-family:%s; font-size:13px; color:%s;">%s</td>
                    <td style="font-family:%s; font-size:14px; color:%s;">%s</td>
                  </tr>
                </table>
              </td>
            </tr>
            """.formatted(SANS, MUTED, label, SANS, INK, value);
    }

    private String tagsSectionHtml(String title, List<String> items) {
        String tags;
        if (items == null || items.isEmpty()) {
            tags = "<span style=\"font-family:" + SANS + "; font-size:13px; color:" + MUTED + ";\">—</span>";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String item : items) {
                sb.append("<span style=\"display:inline-block; background-color:").append(BORDER)
                  .append("; color:#33413F; font-family:").append(SANS)
                  .append("; font-size:12px; padding:4px 12px; border-radius:12px; margin:0 4px 6px 0;\">")
                  .append(escape(item)).append("</span> ");
            }
            tags = sb.toString();
        }
        return """
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:16px;">
              <tr><td style="font-family:%s; font-size:12px; letter-spacing:1.5px; text-transform:uppercase; color:%s; padding-bottom:8px;">%s</td></tr>
              <tr><td>%s</td></tr>
            </table>
            """.formatted(SANS, MUTED, title, tags);
    }

    private String recommendationHtml(RecommendationResponse rec) {
        return """
            <tr>
              <td style="padding:8px 32px 0;">
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
                       style="background-color:%s; border:1px solid %s; border-radius:10px;">
                  <tr>
                    <td style="padding:20px 24px;">
                      <span style="font-family:%s; font-size:12px; letter-spacing:1.5px; text-transform:uppercase; color:%s;">Top foundation match</span>
                      <br><br>
                      <span style="font-family:%s; font-size:20px; font-weight:bold; color:%s;">%s</span>
                      <span style="font-family:%s; font-size:28px; font-weight:bold; color:%s;">&nbsp;&middot;&nbsp;%d%%</span>
                      <br>
                      <span style="font-family:%s; font-size:13px; line-height:1.6; color:#33504D;">%s</span>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            """.formatted(
                TEAL_TINT, TEAL,
                SANS, TEAL_DARK,
                SERIF, TEAL, escape(rec.getFoundationName()),
                SANS, TEAL_DARK, rec.getMatchScore(),
                SANS, escape(rec.getMatchReason())
            );
    }

    private String nullSafe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    /** User-supplied values go into HTML; escape them. */
    private String escape(String value) {
        if (value == null) return "";
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
}
