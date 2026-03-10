package com.ayman.distributed.notification.features.email.controller;

import com.ayman.distributed.notification.features.email.model.EmailRequest;
import com.ayman.distributed.notification.features.email.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendEmail_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        EmailRequest request = new EmailRequest("test@example.com", "Subject", "template", Map.of("key", "value"));

        mockMvc.perform(post("/api/notifications/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(emailService).sendEmail(any(EmailRequest.class));
    }
}
