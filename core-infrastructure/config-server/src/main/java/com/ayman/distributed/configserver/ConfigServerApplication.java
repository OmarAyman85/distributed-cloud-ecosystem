package com.ayman.distributed.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @EnableConfigServer
 * This annotation turns this service into a Centralized Configuration Server.
 * 
 * CORE CONCEPT:
 * Instead of each service having its own 'application.properties' internally,
 * they all "ask" this server for their settings at startup.
 * The settings are pulled from a central Git repository (config-repo).
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}

}
