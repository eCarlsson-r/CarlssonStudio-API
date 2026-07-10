package com.carlssonstudio.api.service;

import com.carlssonstudio.api.config.MailProperties;
import com.carlssonstudio.api.dto.LeadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final EmailTemplateService emailTemplateService;

    @Async
    public void sendLeadNotification(LeadResponse lead) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(
                mailProperties.getFrom(),
                mailProperties.getFromName()
            );
            helper.setTo(mailProperties.getNotificationTo());
            helper.setSubject(
                "🔔 New Lead: " + lead.getName() +
                " (" + lead.getIndustry() + " · " +
                lead.getBuildType() + ")"
            );
            helper.setText(
                emailTemplateService
                    .buildLeadNotificationHtml(lead),
                true  // isHtml
            );

            mailSender.send(message);
            log.info("Lead notification sent for lead id={}",
                lead.getId());

        } catch (Exception e) {
            // Never fail the main flow due to email error
            log.error("Failed to send lead notification " +
                "for lead id={}: {}", lead.getId(), e.getMessage());
        }
    }
}