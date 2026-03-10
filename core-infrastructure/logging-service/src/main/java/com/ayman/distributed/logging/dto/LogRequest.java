package com.ayman.distributed.logging.dto;

import java.time.Instant;

/**
 * Data Transfer Object representing a log entry sent from other microservices.
 */
public record LogRequest(
    String serviceName,   // The name of the service sending the log
    String level,         // INFO, WARN, ERROR, etc.
    String message,       // The actual log message
    String traceId,       // For distributed tracing (optional)
    Instant timestamp     // When the event occurred
) {}
