package com.example.demo.controller.join;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserDTO;
import com.example.demo.service.UserService;
import com.example.demo.util.JWTGenerator;
import com.example.demo.util.JWTValidator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class JoinController {

  private final UserService userservice;

  @GetMapping("/delete")
  public String testdelete(@RequestParam Long uid) {
    userservice.deleteUser(uid);
    return null;
  }

  @PostMapping("/join")
  public Map<String, Object> join(@RequestBody UserDTO dto, HttpServletRequest request) throws Exception {
    String sessionId = request.getSession().getId();

    int result = userservice.join(dto);

    if (result == -1) {
      return Map.of(
          "result", "이미 가입하셨습니다.",
          "resultCode", 400);
    }

    if (result == 0) {
      return Map.of(
          "result", "이미 존재하는 닉네임입니다.",
          "resultCode", 400);
    }

    Map<String, Object> param = Map.of(
        "sessionId", sessionId,
        "userId", dto.getUserId(),
        "pw", dto.getPw(),
        "nickname", dto.getNickname());

    String token = JWTGenerator.generate(param);

    return Map.of(
        "result", "회원가입성공",
        "token", token,
        "resultCode", 100);
  }

  @PostMapping("/login")
  public Map<String, Object> generateToken(@RequestBody UserDTO dto, HttpServletRequest request) throws Exception {

    Map<String, Object> res = userservice.login(dto);
    return res;

    // String sessionId = request.getSession().getId();
    // String token = (String) param.get("token");

    // boolean isValid = JWTValidator.isJwtValid(token);
    // if (!isValid) {
    // return Map.of(
    // "result", "유효하지 않은 토큰입니다.",
    // "resultCode", 400);
    // }

    // Map<String, Object> decoded = JWTValidator.decodePayload(token);

    // 로그인 확인여부 체크

    // ObjectMapper mapper = new ObjectMapper();

    // Map<String, Object> header = Map.of(
    // "alg", "HS256",
    // "typ", "JWT");

    // String headerJson = mapper.writeValueAsString(header);
    // String payloadJson = mapper.writeValueAsString(param);

    // String encodedHeader = JWTGenerator.encodeBase64(headerJson);
    // String encodedPayload = JWTGenerator.encodeBase64(payloadJson);
    // String secret = "tomhoon-secret";

    // String combinedPayload = encodedHeader + "." + encodedPayload;
    // String signature = JWTGenerator.sign(combinedPayload, secret);

    // String token = combinedPayload + "." + signature;

    // return token;

    /**
     * 
     * String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
     * String payloadJson =
     * "{\"sub\":\"1234567890\",\"name\":\"Tom\",\"iat\":1712200000}";
     * 
     * String encodedHeader = JWTGenerator.encodeBase64(headerJson);
     * String encodedPayload = JWTGenerator.encodeBase64(payloadJson);
     * 
     * String secret = "tomhoon-secret";
     * String headerPayload = encodedHeader + "." + encodedPayload;
     * String signature = JWTGenerator.sign(headerPayload, secret);
     * 
     * String token = headerPayload + "." + signature;
     * System.out.println("JWT: " + token);
     * return token;
     */

  }

  @GetMapping("/validtest")
  public Map<String, Object> valid(@RequestParam("token") String token) throws Exception {
    Map<String, Object> result = JWTValidator.decodePayload(token);
    return result;
  }

  @GetMapping("/isJwtValid")
  public boolean isJwtValid(@RequestParam("token") String token) throws Exception {
    boolean result = JWTValidator.isJwtValid(token);
    return result;
  }

}
