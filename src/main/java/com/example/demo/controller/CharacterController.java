package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.demo.dto.MoveMessage; // 캐릭터 모델 클래스 (별도로 생성)
import org.springframework.messaging.simp.SimpMessagingTemplate; // 특정 사용자 또는 목적지로 메시지 전송
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Controller
public class CharacterController {

    // 접속 중인 모든 캐릭터 정보를 관리하는 맵 (세션 ID -> 캐릭터 객체)
    // private final Map<String, MoveMessage> connectedCharacters = new ConcurrentHashMap<>();
    private static final Map<String, MoveMessage> connectedCharacters = new ConcurrentHashMap<>(); // 스레드 안전한 맵 사용
    
    // 특정 사용자 또는 목적지로 메시지를 보내기 위한 객체
    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    public CharacterController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 클라이언트가 "/app/join"으로 메시지를 보내면 이 메서드가 호출됩니다.
    // SimpMessageHeaderAccessor를 사용하여 세션 정보를 가져올 수 있습니다.
    @MessageMapping("/join")
    public void joinGame(SimpMessageHeaderAccessor headerAccessor, @Payload Map<String, String> payload) {
        String sessionId = headerAccessor.getSessionId();
        String characterName = payload.get("name");
        String characterSkill = payload.get("skill");
        System.out.println("클라이언트 게임 참여 요청 - 세션 ID: " + sessionId + ", 캐릭터 이름: " + characterName);
        MoveMessage newCharacter = new MoveMessage(sessionId, Math.random() * 580, Math.random() * 480, characterName, characterSkill);
        newCharacter.setName(characterName); 
        connectedCharacters.put(sessionId, newCharacter);

        System.out.println("캐릭터 생성됨: " + newCharacter);

        messagingTemplate.convertAndSend(
            "/topic/userCharacter",
            Map.of("type", "currentCharacter", "character", newCharacter)
        );

        // 다른 클라이언트들에게 새로운 캐릭터 정보 전송
        messagingTemplate.convertAndSend(
            "/topic/characters",
            Map.of("type", "newCharacter", "character", newCharacter, "name", newCharacter.getName()) 
        );
        // 2. 현재까지 접속 중인 모든 캐릭터 정보를 새로운 클라이언트에게 전송합니다.
        messagingTemplate.convertAndSend(
            // "user", // 대상 사용자 (새로 접속한 클라이언트)
            "/topic/newCharacters", // 개인 큐 목적지 (클라이언트는 이 경로를 구독)
            Map.of("type", "currentCharacters", "characters", connectedCharacters.values()) // 보낼 메시지 페이로드
        );
        System.out.println("모든 캐릭터 정보 전송 - 세션 ID: " + sessionId + ", 캐릭터 수: " + connectedCharacters.size());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        if (sessionId != null) {
            System.out.println("클라이언트 연결 해제: " + sessionId);
            MoveMessage removedCharacter = connectedCharacters.remove(sessionId);

            if (removedCharacter != null) {
                messagingTemplate.convertAndSend(
                    "/topic/characters",
                    Map.of("type", "characterDisconnected", "characterId", removedCharacter.getId())
                );
                System.out.println("제거 메시지 전송: " + removedCharacter.getId() + ", 캐릭터 수: " + connectedCharacters.size());
            }
        }
    }

    @MessageMapping("/move")
    public void moveCharacter(@Payload MoveMessage moveMessage, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String name = moveMessage.getName();
        System.out.println("캐릭터 이동 요청 - 세션 ID: " + sessionId + ", x: " + moveMessage.getX() + ", y: " + moveMessage.getY() + ", name: " + name );
        // 맵에서 캐릭터 정보 가져오기
        MoveMessage character = connectedCharacters.get(sessionId);
        if (character != null) {
            character.setX(moveMessage.getX());
            character.setY(moveMessage.getY());
            character.setName(name);

            // 다른 클라이언트들에게 이동 정보 전파
            messagingTemplate.convertAndSend(
                "/topic/character-moves",
                Map.of("type", "characterMoved", "characterId", character.getId(), "x", character.getX(), "y", character.getY(), "name", character.getName()) // characterId 사용
            );
        }
    }
    
    @MessageMapping("/skill")
    public void skillCharacter(@Payload MoveMessage moveMessage, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String skill = moveMessage.getSkill();
        System.out.println("캐릭터 이동 요청 - 세션 ID: " + sessionId + ", x: " + moveMessage.getX() + ", y: " + moveMessage.getY() + ", skill: " + skill );
        // 맵에서 캐릭터 정보 가져오기
        MoveMessage character = connectedCharacters.get(sessionId);
        if (character != null) {
            character.setX(moveMessage.getX());
            character.setY(moveMessage.getY());
            character.setSkill(skill);

            // 다른 클라이언트들에게 이동 정보 전파
            messagingTemplate.convertAndSend(
                "/topic/character-skill",
                Map.of("type", "characterSkill", "characterId", character.getId(), "x", character.getX(), "y", character.getY(), "skill", character.getSkill())
            );
        }
    }
}