package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO {
  private String roomName;
  private String sender;
  private String content;
  private String type; // "message", "characterMove", "characterSpawn" 등 메시지 타입
  private String characterId;
  private int x;
  private int y;
  private String name;
}
