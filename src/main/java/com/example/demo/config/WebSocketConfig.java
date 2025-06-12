package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.RequestUpgradeStrategy;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy; // Tomcat 사용 시 필요

// import org.springframework.web.socket.server.support.Default
@Configuration
@EnableWebSocketMessageBroker // stomp를 위한것...
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/chat")
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    /**
     * 📕
     * 파이프라인이 두개다.
     * 1) 클라이언트에서 서버로 보냄
     * 2) 서버에서 클라이언트로 보냄
     * 이유) 서로 겹치지 않게 하기 위함
     * 이유) direction clear
     * 이유) Que 형태로써 여러 사용자가 대기 가능
     * 이유) broadCase 가능
     */
    // 📒 파이프라인1: Client to Server
    registry.setApplicationDestinationPrefixes("/app"); // MessageMapping의 prefix // send: /app/room1

    // 📒 파이프라인2: Server to Client
    registry.enableSimpleBroker("/topic");
    // subscribe: /topic/room1
  }
}
