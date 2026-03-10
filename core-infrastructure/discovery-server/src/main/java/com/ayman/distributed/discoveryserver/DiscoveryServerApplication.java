package com.ayman.distributed.discoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @EnableEurekaServer
 * This annotation turns this Spring Boot application into a Netflix Eureka Discovery Server.
 * 
 * CORE CONCEPT:
 * In a microservices architecture, services need to talk to each other.
 * Instead of hardcoding "localhost:8081", services register their NAME here.
 * Eureka keeps a map of "service-name" -> "IP:PORT".
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscoveryServerApplication.class, args);
	}

}
