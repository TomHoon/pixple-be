package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import com.example.demo.dto.MoveMessage; // 캐릭터 모델 클래스 (별도로 생성)
// import com.example.demo.dto.CharacterMoveMessage;
import org.springframework.messaging.handler.annotation.SendTo; // 메시지 전송 어노테이션
import org.springframework.messaging.simp.SimpMessagingTemplate; // 특정 사용자 또는 목적지로 메시지 전송

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;

@Controller // 또는 @Service
public class CharacterController {

    // 접속 중인 모든 캐릭터 정보를 관리하는 맵 (세션 ID -> 캐릭터 객체)
    private final Map<String, MoveMessage> connectedCharacters = new ConcurrentHashMap<>();

    // 특정 사용자 또는 목적지로 메시지를 보내기 위한 객체
    private final SimpMessagingTemplate messagingTemplate;

    public CharacterController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 클라이언트가 "/app/join"으로 메시지를 보내면 이 메서드가 호출됩니다.
    // SimpMessageHeaderAccessor를 사용하여 세션 정보를 가져올 수 있습니다.
    @MessageMapping("/join")
    public void joinGame(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId(); // STOMP 세션 ID
        System.out.println("클라이언트 게임 참여 요청 - 세션 ID: " + sessionId);

            // join 성공 메시지를 해당 클라이언트에게만 전송
            // messagingTemplate.convertAndSendToUser(
            //     sessionId, // 대상 사용자
            //     "/queue/join-status", // 새로운 개인 큐
            //     Map.of("type", "joinSuccess", "message", "게임 참여가 성공적으로 완료되었습니다!", "characterId", sessionId) // 성공 메시지 페이로드
            // );

        // 이미 접속한 캐릭터인지 확인 (재접속 시 중복 생성 방지)
        if (connectedCharacters.containsKey(sessionId)) {
            System.out.println("이미 접속 중인 세션입니다: " + sessionId);
            // 이미 존재하는 캐릭터 정보를 해당 클라이언트에게 다시 보내줄 수 있습니다.
             MoveMessage existingChar = connectedCharacters.get(sessionId);
             messagingTemplate.convertAndSendToUser(
                 sessionId, // 대상 사용자 (세션 ID)
                 "/queue/character", // 대상 목적지 (개인 큐)
                 Map.of("type", "currentCharacter", "character", existingChar) // 보낼 메시지 페이로드
             );
            return; // 이미 존재하면 새로 생성하지 않음
        }

        // 1. 새로운 캐릭터 생성 및 초기 좌표 설정
        String characterId = sessionId; // STOMP 세션 ID를 캐릭터 ID로 사용
        double initialX = Math.random() * 640; // 예시
        double initialY = Math.random() * 540; // 예시

        MoveMessage newCharacter = new MoveMessage(characterId, initialX, initialY);
        connectedCharacters.put(characterId, newCharacter); // 맵에 캐릭터 정보 저장

        System.out.println("캐릭터 생성됨: " + newCharacter);

        // 2. 현재까지 접속 중인 모든 캐릭터 정보를 새로운 클라이언트에게 전송합니다.
        List<MoveMessage> allCharacters = new ArrayList<>(connectedCharacters.values());
        messagingTemplate.convertAndSendToUser(
            sessionId, // 대상 사용자 (새로 접속한 클라이언트)
            "/queue/characters", // 개인 큐 목적지 (클라이언트는 이 경로를 구독)
            Map.of("type", "currentCharacters", "characters", allCharacters) // 보낼 메시지 페이로드
        );

        // 3. 새로운 캐릭터가 접속했음을 다른 모든 클라이언트에게 알립니다.
        // "/topic/characters"를 구독한 모든 클라이언트에게 메시지를 보냅니다.
        messagingTemplate.convertAndSend(
            "/topic/characters", // 대상 목적지 (공개 주제)
            Map.of("type", "newCharacter", "character", newCharacter) // 보낼 메시지 페이로드
        );
    }

    // 클라이언트가 "/app/move"로 메시지를 보내면 이 메서드가 호출됩니다.
    // @RequestBody처럼 메시지 페이로드를 특정 객체로 바로 받을 수 있습니다.
    @MessageMapping("/move")
    // @SendTo 어노테이션을 사용하여 결과를 특정 목적지로 브로드캐스트할 수 있지만,
    // 여기서는 SimpMessagingTemplate을 사용하여 더 세밀하게 메시지를 전송합니다.
    public void moveCharacter(CharacterMoveMessage moveMessage, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId(); // 메시지를 보낸 클라이언트의 세션 ID

        //System.out.println(`캐릭터 이동 요청 수신 - 세션 ID: ${sessionId}, 좌표: (${moveMessage.getX()}, ${moveMessage.getY()})`);

        // 해당 세션 ID에 해당하는 캐릭터 정보를 찾아서 좌표를 업데이트합니다.
        MoveMessage character = connectedCharacters.get(sessionId);
        if (character != null) {
            character.setX(moveMessage.getX());
            character.setY(moveMessage.getY());
            System.out.println("캐릭터 이동 업데이트: " + character);

            // 이동한 캐릭터 정보를 다른 모든 클라이언트에게 브로드캐스트합니다.
            // "/topic/character-moves"를 구독한 모든 클라이언트에게 메시지를 보냅니다.
            messagingTemplate.convertAndSend(
                "/topic/character-moves", // 대상 목적지
                Map.of( // 보낼 메시지 페이로드
                    "type", "characterMoved",
                    "id", character.getId(),
                    "x", character.getX(),
                    "y", character.getY()
                )
            );
        }
    }

    public static class CharacterMoveMessage {
        private double x;
        private double y;
    
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
    
        // 기본 생성자 (역직렬화 시 필요)
        public CharacterMoveMessage() {}
    
        // 생성자
        public CharacterMoveMessage(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}