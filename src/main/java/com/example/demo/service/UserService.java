package com.example.demo.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JWTGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  // 회원가입
  public int join(UserDTO dto) {

    Optional<User> res = userRepository.findByUserId(dto.getUserId());

    // 1. 이미가입자 확인
    if (res.isPresent()) {
      return -1;
    }

    String nickname = dto.getNickname();
    Optional<User> res2 = userRepository.findByNickname(nickname);
    if (res2.isPresent()) {
      return 0;
    }

    User user = User.builder()
        .userId(dto.getUserId())
        .pw(dto.getPw())
        .nickname(dto.getNickname())
        .build();

    User savedUser = userRepository.save(user);
    System.out.println("savedUser  : " + savedUser);

    return 1;
  }

  // 로그인
  public Map<String, Object> login(UserDTO dto) throws Exception {
    Optional<User> res = userRepository.findByUserId(dto.getUserId());

    if (res.isEmpty()) {
      return Map.of(
          "result", "유효한 아이디가 아닙니다",
          "resultCode", "400");
    }

    User user = res.orElseThrow();

    String pw = user.getPw();
    String 작성비번 = dto.getPw();

    if (!작성비번.equals(pw)) {
      return Map.of(
          "result", "비밀번호 맞지 않습니다",
          "resultCode", "400");
    }

    Map<String, Object> map = Map.of(
        "uno", user.getUno(),
        "userId", user.getUserId(),
        "pw", user.getPw(),
        "nickname", user.getNickname());

    String token = JWTGenerator.generate(map);
    return Map.of(
        "result", token,
        "resultCode", "100");
  }

  public String deleteUser(Long uid) {
    userRepository.deleteById(uid);
    return null;
  }

}
