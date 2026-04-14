package com.example.artship.board.model.ws;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomEvent {
    private String type;
    private String roomId;
    private String visitorId;
    private String visitorName;
    private String color;
    private List<Visitor> visitors;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Visitor {
        private String visitorId;
        private String visitorName;
        private String color;
    }
}