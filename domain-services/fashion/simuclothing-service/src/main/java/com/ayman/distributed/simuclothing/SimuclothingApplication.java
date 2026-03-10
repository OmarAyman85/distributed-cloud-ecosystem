package com.ayman.distributed.simuclothing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableDiscoveryClient
public class SimuclothingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimuclothingApplication.class, args);
	}

    @GetMapping("/")
    public String home() {
        return "Hello from Simuclothing Service!";
    }
}
