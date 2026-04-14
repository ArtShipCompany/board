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
    private String userId;
    private String displayName;
    private List<Participant> participants;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Participant {
        private String userId;
        private String displayName;
        private String color;
        private boolean isOwner;
    }
}