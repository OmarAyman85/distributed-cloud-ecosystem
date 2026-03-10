package com.ayman.distributed.delivery.features.bosta.controller;

import com.ayman.distributed.delivery.features.bosta.dto.DeliveryRequest;
import com.ayman.distributed.delivery.features.bosta.service.BostaService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BostaService bostaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createShipment_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        DeliveryRequest request = new DeliveryRequest();
        request.setReceiverFirstName("John");
        request.setReceiverLastName("Doe");
        request.setReceiverPhone("01000000000");
        request.setReceiverEmail("john@example.com");
        request.setCity("Cairo");
        request.setStreetAddress("123 Street");
        request.setCodAmount(100.0);

        when(bostaService.createShipment(any(DeliveryRequest.class)))
                .thenReturn(Map.of("_id", "shipment_123"));

        mockMvc.perform(post("/api/deliveries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._id").value("shipment_123"));
    }
    
    @Test
    void trackShipment_ShouldReturnOk() throws Exception {
        when(bostaService.trackShipment("12345"))
                .thenReturn(Map.of("state", "DELIVERED"));
                
        mockMvc.perform(get("/api/deliveries/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("DELIVERED"));
    }
}
