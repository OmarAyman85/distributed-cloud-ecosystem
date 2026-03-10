package com.ayman.distributed.vox.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for setting up WebSocket message handling with STOMP (Simple Text Oriented Messaging Protocol).
 * This class enables WebSocket support and configures message routing for a chat application.
 */
@Configuration // Marks this class as a Spring configuration class
@EnableWebSocketMessageBroker // Enables WebSocket message handling with a message broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registers STOMP (Simple/Streaming Text Oriented Messaging Protocol) endpoints that clients will use to connect to the WebSocket.
     *
     * @param registry The registry to add WebSocket endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /*
         * Adds a WebSocket endpoint at "/ws" that clients will connect to.
         * - SockJS is enabled as a fallback for clients that do not support WebSockets.
         * - SockJS allows WebSocket-like communication over HTTP if necessary.
         */
        registry.addEndpoint("/ws").withSockJS();
    }

    /**
     * Configures the message broker that will handle routing of messages.
     *
     * @param registry The registry to configure message routing.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /*
         * Defines application destination prefixes.
         * - Messages with destinations starting with "/app" are routed to application controllers.
         * - These messages are handled by methods annotated with @MessageMapping.
         */
        registry.setApplicationDestinationPrefixes("/app");

        /*
         * Enables a simple in-memory message broker that listens on "/topic".
         * - Clients subscribed to destinations like "/topic/chat" will receive messages broadcasted to that topic.
         * - Suitable for simple messaging needs. For scalability, a full-featured broker like RabbitMQ can be used instead.
         */
        registry.enableSimpleBroker("/topic");
    }
}
