package com.ayman.distributed.notification.features.email.service.impl;

import com.ayman.distributed.notification.features.email.model.EmailRequest;
import com.ayman.distributed.notification.features.email.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username:sender@example.com}") // Fallback default
    private String senderEmail;

    @Override
    public void sendEmail(EmailRequest request) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Context context = new Context();
            if (request.getVariables() != null) {
                context.setVariables(request.getVariables());
            }

            // If a template is provided, use Thymeleaf. Otherwise, treat body as plain text (future extension)
             // For now, we assume 'template' is mandatory in this implementation or handle basic text.
            String html = templateEngine.process(request.getTemplate(), context);

            helper.setTo(request.getTo());
            helper.setFrom(senderEmail);
            helper.setSubject(request.getSubject());
            helper.setText(html, true);

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + request.getTo(), e);
        }
    }
}
