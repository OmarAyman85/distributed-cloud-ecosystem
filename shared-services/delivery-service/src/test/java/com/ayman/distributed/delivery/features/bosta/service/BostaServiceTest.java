package com.ayman.distributed.delivery.features.bosta.service;

import com.ayman.distributed.delivery.features.bosta.dto.DeliveryRequest;
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
class BostaServiceTest {

    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;
    
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private BostaService bostaService;

    @BeforeEach
    void setUp() {
        bostaService = new BostaService(restClientBuilder);
        ReflectionTestUtils.setField(bostaService, "baseUrl", "https://app.bosta.co");
        ReflectionTestUtils.setField(bostaService, "apiKey", "test-key");
    }

    @Test
    void createShipment_ShouldReturnResponse_WhenSuccessful() {
        // Arrange
        DeliveryRequest request = new DeliveryRequest();
        request.setReceiverFirstName("John");
        request.setReceiverLastName("Doe");
        request.setReceiverPhone("01000000000");
        request.setReceiverEmail("john@example.com");
        request.setCity("Cairo");
        request.setStreetAddress("123 Street");
        request.setCodAmount(100.0);

        // Mock RestClient chain for POST
        when(restClientBuilder.build()).thenReturn(restClient);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Map.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        
        Map<String, Object> mockResponse = Map.of("_id", "shipment_123");
        when(responseSpec.body(Map.class)).thenReturn(mockResponse);

        // Act
        Map<String, Object> result = bostaService.createShipment(request);

        // Assert
        assertNotNull(result);
        assertEquals("shipment_123", result.get("_id"));
    }
    
    @Test
    void trackShipment_ShouldReturnStatus_WhenSuccessful() {
        // Mock RestClient chain for GET
        when(restClientBuilder.build()).thenReturn(restClient);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.header(anyString(), anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        
        Map<String, Object> mockResponse = Map.of("state", "DELIVERED");
        when(responseSpec.body(Map.class)).thenReturn(mockResponse);
        
        Map<String, Object> result = bostaService.trackShipment("12345");
        
        assertNotNull(result);
        assertEquals("DELIVERED", result.get("state"));
    }
}
