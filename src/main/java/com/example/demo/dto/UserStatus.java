package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserStatus {
  private String userId;
  private String state; // "ONLINE" or "OFFLINE"
}
