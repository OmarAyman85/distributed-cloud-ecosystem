package com.ayman.distributed.payment.features.paymob.service;

import com.ayman.distributed.payment.features.paymob.dto.PaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymobServiceTest {

    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private PaymobService paymobService;

    @BeforeEach
    void setUp() {
        paymobService = new PaymobService(restClientBuilder);
        ReflectionTestUtils.setField(paymobService, "baseUrl", "https://accept.paymob.com");
        ReflectionTestUtils.setField(paymobService, "intentionEndpoint", "/v1/intention/");
        ReflectionTestUtils.setField(paymobService, "secretKey", "test-secret");
        ReflectionTestUtils.setField(paymobService, "publicKey", "test-public");
    }

    @Test
    void initiatePayment_ShouldReturnRedirectUrl_WhenSuccessful() {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100.0);
        request.setCurrency("EGP");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPhoneNumber("01000000000");

        // Mock RestClient chain
        when(restClientBuilder.build()).thenReturn(restClient);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Map.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        
        Map<String, Object> mockResponse = Map.of("client_secret", "secret_123");
        when(responseSpec.body(Map.class)).thenReturn(mockResponse);

        // Act
        String result = paymobService.initiatePayment(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("https://accept.paymob.com/unifiedcheckout/"));
        assertTrue(result.contains("clientSecret=secret_123"));
        assertTrue(result.contains("publicKey=test-public"));
    }
    
    @Test
    void verifyCallback_ShouldReturnTrue_WhenSuccessIsTrue() {
         Map<String, Object> payload = Map.of("success", true);
         boolean result = paymobService.verifyCallback(payload, "hmac");
         assertTrue(result);
    }
}
