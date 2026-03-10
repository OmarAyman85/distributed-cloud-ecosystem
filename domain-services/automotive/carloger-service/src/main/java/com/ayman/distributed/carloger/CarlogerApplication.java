package com.ayman.distributed.carloger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableDiscoveryClient
public class CarlogerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarlogerApplication.class, args);
	}

    @GetMapping("/")
    public String home() {
        return "Hello from Carloger Service!";
    }
}
