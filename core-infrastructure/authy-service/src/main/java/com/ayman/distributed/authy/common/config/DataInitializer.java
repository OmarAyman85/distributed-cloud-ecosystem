package com.ayman.distributed.authy.common.config;

import com.ayman.distributed.authy.features.identity.model.Application;
import com.ayman.distributed.authy.features.identity.model.ApplicationStatus;
import com.ayman.distributed.authy.features.identity.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ApplicationRepository applicationRepository;

    @Override
    public void run(String... args) throws Exception {
        if (applicationRepository.count() == 0) {
            Application app = new Application();
            app.setAppName("Authy");
            app.setAppKey("AUTHY");
            app.setDescription("Default application created by system.");
            app.setStatus(ApplicationStatus.ACTIVE);
            
            applicationRepository.save(app);
            System.out.println("Initialized default application: Authy");
        }
    }
}
