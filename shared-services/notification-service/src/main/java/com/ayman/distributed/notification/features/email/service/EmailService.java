package com.ayman.distributed.notification.features.email.service;

import com.ayman.distributed.notification.features.email.model.EmailRequest;

public interface EmailService {
    void sendEmail(EmailRequest request);
}
