package com.ayman.distributed.notification.features.email.controller;

import com.ayman.distributed.notification.features.email.model.EmailRequest;
import com.ayman.distributed.notification.features.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        emailService.sendEmail(request);
        return ResponseEntity.ok("Email queued/sent successfully");
    }
}
