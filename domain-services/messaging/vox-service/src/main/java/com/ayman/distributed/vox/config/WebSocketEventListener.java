package com.ayman.distributed.vox.config;

import com.ayman.distributed.vox.models.entity.ChatMessage;
import com.ayman.distributed.vox.models.enums.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Event listener for WebSocket events.
 * This class listens for WebSocket disconnection events and handles user disconnections by sending
 * a "LEAVE" message to the public chat topic.
 */
@Component // Marks this class as a Spring-managed component
@RequiredArgsConstructor // Generates a constructor with required dependencies (final fields)
@Slf4j // Enables logging using SLF4J (Simple Logging Facade for Java)
public class WebSocketEventListener {

    /**
     * SimpMessageSendingOperations is used to send STOMP messages to clients.
     * This allows broadcasting messages when a user disconnects.
     */
    private final SimpMessageSendingOperations messageTemplate;

    /**
     * Listens for WebSocket disconnection events.
     * When a user disconnects, it logs the event and notifies all subscribers by sending a "LEAVE" message.
     *
     * @param event The WebSocket session disconnect event.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        // Extracts headers and session attributes from the event
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Retrieves the username from the WebSocket session attributes
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            log.info("User disconnected: {}", username); // Logs the disconnection

            // Builds a chat message of type "LEAVE" to notify other users
            var chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .sender(username)
                    .build();

            // Sends the "LEAVE" message to all clients subscribed to the "/topic/public" destination
            messageTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
