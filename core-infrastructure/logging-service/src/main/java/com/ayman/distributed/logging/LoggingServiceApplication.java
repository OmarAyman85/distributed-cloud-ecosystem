package com.ayman.distributed.logging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * LoggingServiceApplication
 * 
 * CORE CONCEPT:
 * This is a Centralized Logging Service. In a distributed system, 
 * logs are scattered across many servers. This service provides 
 * a single REST endpoint where all other services can "POST" their logs.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class LoggingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoggingServiceApplication.class, args);
    }
}
