package com.example.artship.board.websocket;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private ActiveRoomManager roomManager;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        String[] roomAndVisitor = roomManager.removeTab(sessionId);

        if (roomAndVisitor != null) {
            String roomId = roomAndVisitor[0];
            String visitorId = roomAndVisitor[1];

            Map<String, Object> leaveMessage = new HashMap<>();
            leaveMessage.put("type", "visitor_left");
            leaveMessage.put("visitorId", visitorId);
            leaveMessage.put("roomId", roomId);

            messagingTemplate.convertAndSend("/topic/room/" + roomId, leaveMessage); 
        }
    }
}