package com.ayman.distributed.authy.features.audit.service;

import com.ayman.distributed.authy.features.audit.model.AuditLog;
import com.ayman.distributed.authy.features.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(String actor, String action, String resource, String result, String ipAddress, String details) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .timestamp(LocalDateTime.now())
                    .actor(actor)
                    .action(action)
                    .resource(resource)
                    .result(result)
                    .ipAddress(ipAddress)
                    .details(details)
                    .build();

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage(), e);
        }
    }
}
