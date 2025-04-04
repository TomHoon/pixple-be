package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long uno;

  private String userId;
  private String pw;
  private String nickname;

  public void setUserid(String userId) {
    this.userId = userId;
  }

  public void setPw(String pw) {
    this.pw = pw;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
}
