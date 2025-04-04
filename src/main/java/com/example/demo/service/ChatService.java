package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.demo.dto.ChatDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@CrossOrigin("*")
public class ChatService {

  private final SimpMessagingTemplate template;
  private final RedisTemplate<String, Object> redisTemplate; // ✅ boot 시작시 bean으로 load

  // public ChatService(SimpMessagingTemplate template, RedisTemplate
  // redisTemplate) {
  // this.template = template;
  // this.redisTemplate = redisTemplate;
  // }

  public void sendMessage(ChatDTO cDTO) {

    Map<String, String> message = new HashMap<>();
    message.put("sender", cDTO.getSender());
    message.put("message", cDTO.getContent());

    template.convertAndSend("/topic/" + cDTO.getRoomName(), message.toString().replaceAll("=", ":"));
  }

  public void saveMessage(ChatDTO cDTO) {
    String key = "chat:" + cDTO.getRoomName();

    Map<String, String> message = new HashMap<>();
    message.put("sender", cDTO.getSender());
    message.put("message", cDTO.getContent());

    redisTemplate.opsForList().rightPush(key, message.toString()); // ✅ operations for List의 약자
  }

  public List<String> getMessages(String roomName) {
    String key = "chat:" + roomName;
    List<Object> list = redisTemplate.opsForList().range(key, 0, -1);
    List<String> resultList = new ArrayList<>();

    for (Object item : list) {
      resultList.add(((String) (item)).replaceAll("=", ":"));
    }

    return resultList;
  }

  public List<Object> getRoomNames() {
    Set<String> keys = redisTemplate.keys("chat:*");

    List<Object> list = new ArrayList<>();

    for (String key : keys) {
      String roomName = key.replace("chat:", "");
      list.add(roomName);
    }

    return list;
  }

}
