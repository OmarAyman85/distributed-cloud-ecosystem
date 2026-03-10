package com.ayman.distributed.payment.features.paymob.controller;

import com.ayman.distributed.payment.features.paymob.dto.PaymentRequest;
import com.ayman.distributed.payment.features.paymob.service.PaymobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymobService paymobService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void initiatePayment_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100.0);
        request.setCurrency("EGP");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPhoneNumber("01000000000");

        when(paymobService.initiatePayment(any(PaymentRequest.class)))
                .thenReturn("https://paymob.com/redirect");

        mockMvc.perform(post("/api/payments/initiate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redirectUrl").value("https://paymob.com/redirect"));
    }

    @Test
    void handleCallback_ShouldReturnOk() throws Exception {
        Map<String, Object> payload = Map.of("success", true);

        when(paymobService.verifyCallback(any(), any())).thenReturn(true);

        mockMvc.perform(post("/api/payments/callback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .param("hmac", "test-hmac"))
                .andExpect(status().isOk());
    }
}
