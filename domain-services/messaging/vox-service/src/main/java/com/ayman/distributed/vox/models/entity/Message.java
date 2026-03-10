package com.ayman.distributed.vox.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long senderId;

    @NotNull
    private Long receiverId;

    @NotBlank
    private String content;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private boolean read;
}
