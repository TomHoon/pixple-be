// package com.example.demo.controller;

// import com.example.demo.dto.MoveMessage;
// import com.example.demo.model.Character;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.handler.annotation.SendTo;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.messaging.simp.annotation.SendToUser;
// import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.socket.messaging.SessionConnectEvent;
// import org.springframework.web.socket.messaging.SessionDisconnectEvent;
// import org.springframework.context.event.EventListener;

// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;

// @Controller
// public class GameController {

//     // 현재 접속 중인 캐릭터 목록 (동시성 고려 ConcurrentHashMap 사용)
//     private final Map<String, Character> activeCharacters = new ConcurrentHashMap<>();

//     @Autowired
//     private SimpMessagingTemplate messagingTemplate;

//     // 클라이언트 연결 시 호출
//     @EventListener
//     public void handleSessionConnectEvent(SessionConnectEvent event) {
//         StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//         String sessionId = headerAccessor.getSessionId(); // 세션 ID를 사용자 ID로 사용

//         // 새로운 캐릭터 생성 (초기 위치 설정)
//         Character newCharacter = new Character(sessionId, 100, 100);
//         activeCharacters.put(sessionId, newCharacter);

//         // 모든 클라이언트에게 새로운 사용자가 접속했음을 알림
//         // /topic/public 구독자들에게 메시지를 보냅니다.
//         messagingTemplate.convertAndSend("/topic/public", newCharacter);

//         System.out.println("User connected: " + sessionId);

//         // 새로 접속한 클라이언트에게 현재 접속 중인 모든 캐릭터 목록을 보냄
//         // /user/{sessionId}/queue/private 으로 메시지를 보냅니다.
//         // 클라이언트는 "/user/queue/private"를 구독해야 합니다.
//          messagingTemplate.convertAndSendToUser(sessionId, "/queue/private", activeCharacters.values());
//     }

//     // 클라이언트 연결 해제 시 호출
//     @EventListener
//     public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
//         StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//         String sessionId = headerAccessor.getSessionId();

//         // 캐릭터 목록에서 제거
//         Character removedCharacter = activeCharacters.remove(sessionId);

//         if (removedCharacter != null) {
//             // 모든 클라이언트에게 사용자가 나갔음을 알림
//             // 나간 사용자의 ID를 메시지에 포함하여 보냅니다.
//             messagingTemplate.convertAndSend("/topic/public", "user_left:" + sessionId);
//             System.out.println("User disconnected: " + sessionId);
//         }
//     }

//     // 클라이언트로부터 이동 메시지를 받을 때 호출
//     // 클라이언트는 "/app/move"로 메시지를 보냅니다.
//     @MessageMapping("/move")
//     public void moveCharacter(MoveMessage message, StompHeaderAccessor headerAccessor) {
//         String sessionId = headerAccessor.getSessionId();
//         Character character = activeCharacters.get(sessionId);

//         if (character != null) {
//             character.move(message.getDirection()); // 캐릭터 위치 업데이트

//             // 변경된 캐릭터 위치를 모든 클라이언트에게 브로드캐스트
//             // /topic/public 구독자들에게 업데이트된 Character 객체를 보냅니다.
//             messagingTemplate.convertAndSend("/topic/public", character);
//         }
//     }

//     // 초기 접속 시 현재 사용자 목록을 보내는 엔드포인트 (위 handleSessionConnectEvent에서 처리하므로 이 메서드는 필요 없을 수도 있습니다)
//     // @MessageMapping("/join") // 클라이언트가 접속 후 바로 호출하여 초기 목록을 받을 수도 있습니다.
//     // @SendToUser("/queue/private")
//     // public Collection<Character> sendCurrentUserList(StompHeaderAccessor headerAccessor) {
//     //     // 이 메서드는 @EventListener handleSessionConnectEvent 에서 대체 가능
//     //     return activeCharacters.values();
//     // }
// }