package com.example.demo.util.websocket;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.demo.dto.UserStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

  private final RedisTemplate<String, String> redisTemplate;
  private final SimpMessagingTemplate messagingTemplate;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    System.out.println("User connected: " + event.getMessage().getHeaders());

    String userId = getUserIdFromEvent(event);
    if (userId != null) {
      redisTemplate.opsForSet().add("online-users", userId);

      messagingTemplate.convertAndSend("/topic/status", new UserStatus(userId, "ONLINE"));

      System.out.println("User connected: " + userId);
    }
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    System.out.println("User disconnected: " + event.getSessionId());

    String userId = getUserIdFromEvent(event); // same logic here

    if (userId != null) {
      redisTemplate.opsForSet().remove("online-users", userId);

      messagingTemplate.convertAndSend("/topic/status", new UserStatus(userId, "OFFLINE"));

      System.out.println("User disconnected: " + userId);
    }
  }

  private String getUserIdFromEvent(AbstractSubProtocolEvent event) {
    // You can extract user info from Principal
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    List<String> userIdHeader = accessor.getNativeHeader("userId");

    return (userIdHeader != null && !userIdHeader.isEmpty()) ? userIdHeader.get(0) : null;
  }
}
