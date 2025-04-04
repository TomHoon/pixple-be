package com.example.demo.util;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JWTGenerator {

  // public JWTGenerator() {

  // }

  // public static void main(String[] args) throws Exception {
  // JWTGenerator jwt = new JWTGenerator();

  // String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
  // String payloadJson =
  // "{\"sub\":\"1234567890\",\"name\":\"Tom\",\"iat\":1712200000}";

  // String encodedHeader = jwt.encodeBase64(headerJson);
  // String encodedPayload = jwt.encodeBase64(payloadJson);

  // String secret = "tomhoon-secret";
  // String headerPayload = encodedHeader + "." + encodedPayload;
  // String signature = jwt.sign(headerPayload, secret);

  // String token = headerPayload + "." + signature;
  // System.out.println("JWT: " + token);

  // jwt.decodePayload(token);

  // }

  public static String generate(Map<String, Object> param) throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    Map<String, Object> header = Map.of(
        "alg", "HS256",
        "typ", "JWT");

    Map<String, Object> generateParam = new HashMap<>();
    generateParam.put("iat", System.currentTimeMillis() / 1000); // 발행시간 추가
    generateParam.put("sessionId", param.get("sessionId"));
    generateParam.put("userId", param.get("userId"));
    generateParam.put("pw", param.get("pw"));
    generateParam.put("nickname", param.get("nickname"));

    String headerJson = mapper.writeValueAsString(header);
    String payloadJson = mapper.writeValueAsString(generateParam);

    String encodedHeader = JWTGenerator.encodeBase64(headerJson);
    String encodedPayload = JWTGenerator.encodeBase64(payloadJson);
    String secret = "tomhoon-secret";

    String combinedPayload = encodedHeader + "." + encodedPayload;
    String signature = JWTGenerator.sign(combinedPayload, secret);

    String token = combinedPayload + "." + signature;

    return token;
  }

  /**
   * 1. JWT 만들기 사전 작업.
   * Header, Payload 모두 base64로 인코딩
   */
  public static String encodeBase64(String param) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(param.getBytes());
  }

  /**
   * 2. HMACSHA256으로 해싱처리
   * 2-1. 시크릿 키 넣고 header,payload랑 같이 해싱 돌리기
   * 2-2. 나온 값 또 base64로 인코딩;;;
   */
  public static String sign(String data, String secret) throws Exception {
    Mac hmacSha256 = Mac.getInstance("HmacSHA256"); // HMACSHA256으로 만들어주는 애 호출
    SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256"); // secret키 바이트로 생성
    hmacSha256.init(secretKey); // secret키 미리 넣어두기
    byte[] signatureBytes = hmacSha256.doFinal(data.getBytes()); // 시크릿키 넣고 돌리기
    return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes); // 나온 값 base64로 다시 인코딩
  }

}
