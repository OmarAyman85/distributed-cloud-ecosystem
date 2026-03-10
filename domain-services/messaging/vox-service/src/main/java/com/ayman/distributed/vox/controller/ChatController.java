package com.ayman.distributed.vox.controller;

import com.ayman.distributed.vox.models.entity.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * Controller for handling WebSocket chat messages.
 * This class defines endpoints for sending messages and adding users to the chat.
 */
@Controller // Marks this class as a Spring WebSocket controller
public class ChatController {

    /**
     * Handles incoming chat messages from clients and broadcasts them to all subscribers.
     *
     * @param chatMessage The chat message sent by a user.
     * @return The chat message, which is broadcast to all subscribers of "/topic/public".
     */
    @MessageMapping("/chat.sendMessage") // Clients send messages to "/app/chat.sendMessage"
    @SendTo("/topic/public") // The message is broadcast to all subscribers of "/topic/public"
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage; // Simply returns the received message for broadcasting
    }

    /**
     * Handles new users joining the chat.
     * - Stores the username in the WebSocket session attributes.
     * - Broadcasts a message notifying all users that a new user has joined.
     *
     * @param chatMessage The message containing the username of the new user.
     * @param headerAccessor Provides access to WebSocket headers and session attributes.
     * @return The chat message, which is broadcast to all subscribers of "/topic/public".
     */
    @MessageMapping("/chat.addUser") // Clients send new user data to "/app/chat.addUser"
    @SendTo("/topic/public") // Broadcasts the new user event to all subscribers of "/topic/public"
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Store the username in the WebSocket session attributes
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage; // Returns the message to notify other users
    }
}
