package com.ayman.distributed.notification.features.email.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    private String to;
    private String subject;
    private String template; // Name of the Thymeleaf template (e.g., "welcome")
    private Map<String, Object> variables; // Variables to replace in the template
}
