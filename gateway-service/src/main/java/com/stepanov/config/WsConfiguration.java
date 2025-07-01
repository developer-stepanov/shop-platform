package com.stepanov.config;

import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Spring configuration that enables a simple STOMP broker for the
 * gateway service.
 *
 * <p>Routing rules:</p>
 * <ul>
 *   <li><strong>Inbound (client → server):</strong> messages are sent to
 *       destinations starting with {@code /app/**}.  These are handled by the
 *       {@code @MessageMapping} methods in {@code WsController}.</li>
 *   <li><strong>Outbound (server → clients):</strong> messages that the
 *       application publishes to {@code /topic/**} are broadcast by the broker
 *       to all subscribed clients.</li>
 * </ul>
 *
 * <h2>Endpoint</h2>
 * <p>The browser (or SockJS fallback) connects to
 * <code>ws://&lt;host&gt;:&lt;port&gt;/ws</code>.
 * In development that defaults to <code>ws://localhost:8080/ws</code>.</p>
 *
 * <h2>Security note</h2>
 * <p>{@code setAllowedOriginPatterns("*")} is convenient during development
 * but should be narrowed down (or replaced with CORS rules in Spring Security)
 * for production deployments.</p>
 *
 * @author  Maxim Stepanov
 */

@Configuration
@EnableWebSocketMessageBroker
public class WsConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // Browser will connect to: ws://localhost:8080/ws
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        // client sends to /app/**
        registry.setApplicationDestinationPrefixes("/app");

        // server broadcasts to /topic/**
        registry.enableSimpleBroker("/topic");
    }
}
