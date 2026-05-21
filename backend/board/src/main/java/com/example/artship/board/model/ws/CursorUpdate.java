package com.example.artship.board.model.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorUpdate {
    private String type = "cursor_update"; 
    private String visitorId;
    private String roomId;
    private String visitorName;
    private String color;
    private double x;
    private double y;
    private long timestamp;
}