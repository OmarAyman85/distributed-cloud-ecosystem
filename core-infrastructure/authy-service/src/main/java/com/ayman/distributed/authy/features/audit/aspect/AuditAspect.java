package com.ayman.distributed.authy.features.audit.aspect;

import com.ayman.distributed.authy.features.audit.annotation.Auditable;
import com.ayman.distributed.authy.features.audit.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @AfterReturning(pointcut = "@annotation(com.ayman.distributed.authy.features.audit.annotation.Auditable)", returning = "result")
    public void logSuccess(JoinPoint joinPoint, Object result) {
        logAudit(joinPoint, "SUCCESS", result);
    }

    @AfterThrowing(pointcut = "@annotation(com.ayman.distributed.authy.features.audit.annotation.Auditable)", throwing = "exception")
    public void logFailure(JoinPoint joinPoint, Throwable exception) {
        logAudit(joinPoint, "FAILURE", exception.getMessage());
    }

    private void logAudit(JoinPoint joinPoint, String resultStatus, Object details) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Auditable auditable = method.getAnnotation(Auditable.class);

        String action = auditable.action();
        String resource = auditable.resource();
        
        // Simple way to get current username if available in security context
        // In a real app, inject SecurityContextHolder
        String actor = "system"; 
        try {
             if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null) {
                 actor = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
             }
        } catch (Exception ignored) {}

        String ipAddress = "unknown";
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ipAddress = request.getRemoteAddr();
            }
        } catch (Exception ignored) {}

        auditService.log(actor, action, resource, resultStatus, ipAddress, details != null ? details.toString() : "");
    }
}
