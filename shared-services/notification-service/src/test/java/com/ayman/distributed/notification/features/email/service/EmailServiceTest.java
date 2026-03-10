package com.ayman.distributed.notification.features.email.service;

import com.ayman.distributed.notification.features.email.model.EmailRequest;
import com.ayman.distributed.notification.features.email.service.impl.EmailServiceImpl;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(javaMailSender, templateEngine);
        org.springframework.test.util.ReflectionTestUtils.setField(emailService, "senderEmail", "test@example.com");
    }

    @Test
    void sendEmail_ShouldProcessTemplateAndSendMail() {
        // Arrange
        EmailRequest request = new EmailRequest("test@example.com", "Welcome", "welcome", Map.of("name", "John"));
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("welcome"), any(Context.class))).thenReturn("<html>Welcome John!</html>");

        // Act
        emailService.sendEmail(request);

        // Assert
        verify(templateEngine).process(eq("welcome"), any(Context.class));
        verify(javaMailSender).send(mimeMessage);
    }
}
