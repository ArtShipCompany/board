package com.example.artship.board.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ActiveRoomManager {

    private final Map<String, Map<String, String>> activeRooms = new ConcurrentHashMap<>();
    
    private final Map<String, String[]> sessionMap = new ConcurrentHashMap<>();

    public void addUserToRoom(String sessionId, String roomId, String visitorId, String visitorName) {
        activeRooms.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                   .put(visitorId, visitorName);
        
        if (sessionId != null) {
            sessionMap.put(sessionId, new String[]{roomId, visitorId});
        }
    }

    public void removeUserFromRoom(String roomId, String visitorId) {
        Map<String, String> visitors = activeRooms.get(roomId);
        if (visitors != null) {
            visitors.remove(visitorId);
            if (visitors.isEmpty()) {
                activeRooms.remove(roomId);
            }
        }
    }

    public String[] removeTab(String sessionId) {
        String[] roomAndVisitor = sessionMap.remove(sessionId);
        if (roomAndVisitor != null) {
            String roomId = roomAndVisitor[0];
            String visitorId = roomAndVisitor[1];
            removeUserFromRoom(roomId, visitorId);
            return roomAndVisitor;
        }
        return null;
    }

    public Map<String, Map<String, String>> getActiveRooms() {
        return activeRooms;
    }
}