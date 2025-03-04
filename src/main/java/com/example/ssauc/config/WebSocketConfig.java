package com.example.ssauc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /topic, /queue 로 시작하는 메시지를 브로커가 처리
        registry.enableSimpleBroker("/topic", "/queue");
        // 클라이언트에서 메시지를 보낼 때 prefix
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트의 웹소켓 연결 주소: ws://localhost:8080/ws-stomp
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*") // 모든 Origin 허용 + allowCredentials
                .withSockJS();  // SockJS fallback

    }
}