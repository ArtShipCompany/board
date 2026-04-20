package com.example.artship.board.model.ws;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionData {
    private String actionId;
    private String type;
    private String targetId;
    private String visitorId;
    private String roomId;
    private Map<String, Object> payload;
    private long timestamp;
}