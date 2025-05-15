package com.example.demo.dto;

// @Data
// @AllArgsConstructor
// @NoArgsConstructor
public class MoveMessage {
    private String id;
    private double x;
    private double y;
    // 필요하다면 String name; 등 다른 속성 추가

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    // 생성자
    public MoveMessage(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    // 기본 생성자 (Jackson이 역직렬화 시 필요)
    public MoveMessage() {}

    @Override
    public String toString() {
        return "Character{" +
            "id='" + id + '\'' +
            ", x=" + x +
            ", y=" + y +
            '}';
    }
}