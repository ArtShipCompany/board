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
    private String userId;
    private String roomId;
    private String displayName;
    private String color;
    private double x;
    private double y;
    private long timestamp;
}