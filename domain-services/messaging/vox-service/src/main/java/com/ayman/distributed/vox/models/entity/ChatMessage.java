package com.ayman.distributed.vox.models.entity;


import com.ayman.distributed.vox.models.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String content;

    private String sender;

    private MessageType type;
}
