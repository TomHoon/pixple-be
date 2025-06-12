package com.example.demo.controller;

import java.util.Map;

import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.demo.dto.ChatDTO;
import com.example.demo.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
// @CrossOrigin(origins = "*") // Allow CORS for this controller
@RequiredArgsConstructor
public class WebsocketController {

  private final ChatService chatService;

  // public WebsocketController(SimpMessagingTemplate messagingTemplate) {
  // this.messagingTemplate = messagingTemplate;
  // }

  @MessageMapping("/send") // ✅ 1. 받는 경로 send message ex) /app/send
  // @SendTo("/topic/messages") // subscribe or send to topic.
  public void sendMessage(@Payload ChatDTO cDTO) {

    // Save to Redis
    chatService.saveMessage(cDTO);
    chatService.sendMessage(cDTO);
    // messagingTemplate.convertAndSend("/topic/" + roomName, msg); // ✅ 2. 보낸다
  }

  // 📍 테스트용 코드입니다
  @MessageMapping("/greetings") // 📒
  @SendTo("/topic/greetings")
  public String sendTest(String message, @Headers MessageHeaders headers)
      throws JsonMappingException, JsonProcessingException {
    String 세션아이디 = (String) headers.get("simpSessionId");
    ObjectMapper objectMapper = new ObjectMapper();

    Map<String, Object> map = objectMapper.readValue(message, Map.class);
    map.put("sessionId", 세션아이디);
    System.out.println("map ::::  " + map);
    return message;
  }
}
