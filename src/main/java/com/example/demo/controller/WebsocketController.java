package com.example.demo.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.example.demo.dto.ChatDTO;
import com.example.demo.service.ChatService;

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
}
