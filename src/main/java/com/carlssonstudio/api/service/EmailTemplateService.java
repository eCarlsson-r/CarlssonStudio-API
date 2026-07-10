package com.carlssonstudio.api.service;

import com.carlssonstudio.api.dto.LeadResponse;
import com.carlssonstudio.api.dto.RecommendationResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmailTemplateService {

    public String buildLeadNotificationHtml(LeadResponse lead) {
        RecommendationResponse top = lead.getRecommendations()
                .isEmpty() ? null : lead.getRecommendations().get(0);

        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <style>
                body { font-family: Arial, sans-serif; background: #f5f5f5;
                       margin: 0; padding: 20px; }
                .card { background: #ffffff; border-radius: 8px;
                        max-width: 600px; margin: 0 auto;
                        padding: 32px; border: 1px solid #e0e0e0; }
                .header { border-bottom: 3px solid #6366f1;
                          padding-bottom: 16px; margin-bottom: 24px; }
                .header h1 { color: #1a1a2e; font-size: 22px; margin: 0; }
                .header p { color: #666; margin: 4px 0 0; font-size: 14px; }
                .badge { display: inline-block; background: #6366f1;
                         color: white; padding: 4px 12px;
                         border-radius: 20px; font-size: 12px;
                         font-weight: bold; margin-bottom: 16px; }
                .section { margin-bottom: 20px; }
                .section h3 { color: #333; font-size: 13px;
                              text-transform: uppercase;
                              letter-spacing: 0.05em;
                              margin: 0 0 8px; }
                .field { display: flex; padding: 6px 0;
                         border-bottom: 1px solid #f0f0f0; }
                .field .label { color: #888; font-size: 13px;
                                min-width: 140px; }
                .field .value { color: #1a1a2e; font-size: 13px;
                                font-weight: 500; }
                .tag { display: inline-block; background: #f0f0f0;
                       color: #444; padding: 3px 10px;
                       border-radius: 12px; font-size: 12px;
                       margin: 2px; }
                .recommendation { background: #f0f0ff;
                                  border: 1px solid #6366f1;
                                  border-radius: 8px; padding: 16px;
                                  margin-top: 8px; }
                .rec-name { font-size: 18px; font-weight: bold;
                            color: #6366f1; }
                .rec-score { font-size: 28px; font-weight: bold;
                             color: #1a1a2e; }
                .rec-reason { color: #555; font-size: 13px;
                              margin-top: 8px; line-height: 1.5; }
                .footer { margin-top: 24px; padding-top: 16px;
                          border-top: 1px solid #e0e0e0;
                          text-align: center; color: #aaa;
                          font-size: 12px; }
                .cta { display: block; text-align: center;
                       background: #6366f1; color: white;
                       padding: 12px 24px; border-radius: 6px;
                       text-decoration: none; font-weight: bold;
                       margin: 20px 0; font-size: 14px; }
              </style>
            </head>
            <body>
              <div class="card">
                <div class="header">
                  <h1>🔔 New Lead — Carlsson Studio</h1>
                  <p>A new project inquiry has been submitted.</p>
                </div>

                <span class="badge">NEW LEAD</span>

                <div class="section">
                  <h3>Contact Details</h3>
                  <div class="field">
                    <span class="label">Name</span>
                    <span class="value">%s</span>
                  </div>
                  <div class="field">
                    <span class="label">Email</span>
                    <span class="value">%s</span>
                  </div>
                  <div class="field">
                    <span class="label">Company</span>
                    <span class="value">%s</span>
                  </div>
                  <div class="field">
                    <span class="label">Company Size</span>
                    <span class="value">%s employees</span>
                  </div>
                </div>

                <div class="section">
                  <h3>Project Details</h3>
                  <div class="field">
                    <span class="label">Industry</span>
                    <span class="value">%s</span>
                  </div>
                  <div class="field">
                    <span class="label">Build Type</span>
                    <span class="value">%s</span>
                  </div>
                </div>

                <div class="section">
                  <h3>Problems to Solve</h3>
                  %s
                </div>

                <div class="section">
                  <h3>Required Features</h3>
                  %s
                </div>

                %s

                <a href="mailto:%s" class="cta">
                  Reply to %s →
                </a>

                <div class="footer">
                  Carlsson Studio · Independent Software Studio
                  · Medan, Indonesia
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                lead.getName(),
                lead.getEmail(),
                nullSafe(lead.getCompany(), "—"),
                nullSafe(lead.getCompanySize(), "—"),
                lead.getIndustry(),
                lead.getBuildType(),
                tagsHtml(lead.getProblems()),
                tagsHtml(lead.getFeatures()),
                top != null ? recommendationHtml(top) : "",
                lead.getEmail(),
                lead.getName()
            );
    }

    private String tagsHtml(List<String> items) {
        if (items == null || items.isEmpty()) return "<span>—</span>";
        StringBuilder sb = new StringBuilder();
        for (String item : items) {
            sb.append("<span class=\"tag\">").append(item)
              .append("</span>");
        }
        return sb.toString();
    }

    private String recommendationHtml(RecommendationResponse rec) {
        return """
            <div class="section">
              <h3>Top Foundation Match</h3>
              <div class="recommendation">
                <div class="rec-name">%s</div>
                <div class="rec-score">%d%% match</div>
                <div class="rec-reason">%s</div>
              </div>
            </div>
            """.formatted(
                rec.getFoundationName(),
                rec.getMatchScore(),
                rec.getMatchReason()
            );
    }

    private String nullSafe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}