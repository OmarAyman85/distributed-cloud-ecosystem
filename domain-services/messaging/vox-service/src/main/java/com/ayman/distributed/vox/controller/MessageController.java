package com.ayman.distributed.vox.controller;

import com.ayman.distributed.vox.models.entity.Message;
import com.ayman.distributed.vox.repository.MessageRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepository repository;

    @PostMapping
    public ResponseEntity<Message> sendMessage(@Valid @RequestBody Message message) {
        return ResponseEntity.ok(repository.save(message));
    }

    @GetMapping("/received/{userId}")
    public ResponseEntity<List<Message>> getReceivedMessages(@PathVariable Long userId) {
        return ResponseEntity.ok(repository.findByReceiverId(userId));
    }

    @GetMapping("/sent/{userId}")
    public ResponseEntity<List<Message>> getSentMessages(@PathVariable Long userId) {
        return ResponseEntity.ok(repository.findBySenderId(userId));
    }
}
