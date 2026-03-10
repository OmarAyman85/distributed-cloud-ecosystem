package com.ayman.distributed.authy.features.audit.service;

import com.ayman.distributed.authy.features.audit.model.AuditLog;
import com.ayman.distributed.authy.features.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    void log_ShouldSaveAuditLog() {
        auditService.log("user", "LOGIN", "session", "SUCCESS", "127.0.0.1", "details");

        verify(auditLogRepository).save(any(AuditLog.class));
    }
}
