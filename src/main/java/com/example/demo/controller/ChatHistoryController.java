package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat/history")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ChatHistoryController {

  private final ChatService chatService;

  @GetMapping("/getHistory")
  public List<String> getChatHistory(@RequestParam("roomName") String roomName) {
    return chatService.getMessages(roomName);
  }

  @GetMapping("/getRoomNames")
  public List<Object> getRoomNames() {
    return chatService.getRoomNames();
  }

}
