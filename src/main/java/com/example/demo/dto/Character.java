package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Character {
    private String id; // WebSocket Session ID 또는 사용자 ID
    private String name;
    private String color;
    private int x;
    private int y;

    public Character() {}

    public Character(String id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        // 초기 위치는 랜덤하게 설정하거나 기본값을 줄 수 있습니다.
        this.x = (int) (Math.random() * 640);
        this.y = (int) (Math.random() * 540);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}