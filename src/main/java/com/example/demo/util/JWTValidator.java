package com.example.demo.util;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

/**
 * JWT 기간 남은지 확인
 * JWT 안에 있는 Payload 꺼내서 쓰기
 */
public class JWTValidator {

  public static Map<String, Object> decodePayload(String jwt) throws Exception {
    String[] parts = jwt.split("\\.");
    String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(payloadJson, Map.class);
  }

  public static boolean isJwtValid(String jwt) throws Exception {
    long now = System.currentTimeMillis() / 1000; // current time in seconds

    Map<String, Object> payload = decodePayload(jwt);

    long iat = ((Number) payload.get("iat")).longValue(); // issued at time
    long expiresAt = iat + (30 * 60); // 30분 초단위

    return now <= expiresAt;
  }
}
