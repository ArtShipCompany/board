package com.example.artship.board.controller.ws;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.artship.board.model.ws.ActionData;
import com.example.artship.board.model.ws.CursorUpdate;
import com.example.artship.board.model.ws.JoinRequest;
import com.example.artship.board.model.ws.LeaveRequest;
import com.example.artship.board.model.ws.RoomEvent;
import com.example.artship.board.model.ws.StrokeData;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DrawingWebSocketController {

    private final ConcurrentHashMap<String, RoomState> rooms = new ConcurrentHashMap<>();
    private static final int MAX_VISITORS = 10;

    @MessageMapping("/room/{roomId}/join")
    @SendTo("/topic/room/{roomId}")
    public RoomEvent joinRoom(@DestinationVariable String roomId, @Payload JoinRequest request) {

        RoomState room = rooms.computeIfAbsent(roomId, k -> new RoomState());

        if (room.getVisitors().size() >= MAX_VISITORS) {
            return RoomEvent.builder()
                    .type("room_full")
                    .roomId(roomId)
                    .build();
        }

        RoomEvent.Visitor visitor = RoomEvent.Visitor.builder()
                .visitorId(request.getVisitorId())
                .visitorName(request.getVisitorName())
                .color(request.getColor())
                .build();

        room.getVisitors().put(request.getVisitorId(), visitor);

        log.info("Visitor {} ({}) joined room {}",
                request.getVisitorName(), request.getVisitorId(), roomId);

        return RoomEvent.builder()
                .type("visitor_joined")
                .roomId(roomId)
                .visitorId(request.getVisitorId())
                .visitorName(request.getVisitorName())
                .color(request.getColor())
                .visitors(List.copyOf(room.getVisitors().values()))
                .build();
    }

    @MessageMapping("/room/{roomId}/cursor")
    @SendTo("/topic/room/{roomId}")
    public CursorUpdate updateCursor(@DestinationVariable String roomId, @Payload CursorUpdate update) {
        return update;
    }

    @MessageMapping("/room/{roomId}/stroke")
    @SendTo("/topic/room/{roomId}")
    public StrokeData drawStroke(@DestinationVariable String roomId, @Payload StrokeData stroke) {
        return stroke;
    }

    @MessageMapping("/room/{roomId}/action")
    @SendTo("/topic/room/{roomId}")
    public ActionData executeAction(@DestinationVariable String roomId, @Payload ActionData action) {
        return action;
    }

    @MessageMapping("/room/{roomId}/leave")
    @SendTo("/topic/room/{roomId}")
    public RoomEvent leaveRoom(@DestinationVariable String roomId, @Payload LeaveRequest request) {

        RoomState room = rooms.get(roomId);
        if (room != null) {
            room.getVisitors().remove(request.getVisitorId());
            if (room.getVisitors().isEmpty()) {
                rooms.remove(roomId);
                log.info("Room {} closed (no visitors)", roomId);
            }
        }

        log.info("Visitor {} left room {}", request.getVisitorId(), roomId);

        List<RoomEvent.Visitor> visitors = (room != null)
                ? List.copyOf(room.getVisitors().values())
                : List.of();

        return RoomEvent.builder()
                .type("visitor_left")
                .roomId(roomId)
                .visitorId(request.getVisitorId())
                .visitors(visitors)
                .build();
    }

    private static class RoomState {

        private final ConcurrentHashMap<String, RoomEvent.Visitor> visitors;

        public RoomState() {
            this.visitors = new ConcurrentHashMap<>();
        }

        public Map<String, RoomEvent.Visitor> getVisitors() {
            return visitors;
        }
    }
}
