package com.ayman.distributed.logging.service;

import com.ayman.distributed.logging.dto.LogRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service layer for Logging.
 * 
 * CORE CONCEPT:
 * In a standard Spring Boot application, we separate the "What" (Controller) 
 * from the "How" (Service). 
 * 
 * The Controller handles HTTP requests, while the Service handles the 
 * business logic (like deciding where to save the log, or filtering 
 * sensitive information before saving).
 */
@Service
@Slf4j
public class LoggingService {

    /**
     * Processes the log request.
     * 
     * Imagine this method as a pipeline:
     * 1. Check if the message contains sensitive data (PII).
     * 2. Determine if it should be sent to a long-term storage (like DB).
     * 3. Finally, output to the console for real-time monitoring.
     */
    public void processLog(LogRequest request) {
        // Here we could add logic to save to a database if the level is ERROR
        if ("ERROR".equalsIgnoreCase(request.level())) {
            // saveToDatabase(request);
        }

        log.info("[CENTRAL-SECURE-LOG] [{}] [{}] - {}", 
            request.serviceName(), 
            request.level(), 
            request.message());
    }
}
