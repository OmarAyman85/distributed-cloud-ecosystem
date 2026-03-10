package com.ayman.distributed.logging.controller;

import com.ayman.distributed.logging.dto.LogRequest;
import com.ayman.distributed.logging.service.LoggingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to receive and process logs from other services in the ecosystem.
 * This acts as a centralized sink for distributed logging.
 */
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LoggingController {

    private final LoggingService loggingService;

    /**
     * Receives a log entry and delegates processing to the service layer.
     * 
     * EDUCATIONAL TIP:
     * Notice how we injected 'LoggingService' via constructor (using @RequiredArgsConstructor).
     * This is the preferred way of Dependency Injection in Spring.
     */
    @PostMapping
    public void receiveLog(@RequestBody LogRequest request) {
        loggingService.processLog(request);
    }
}
