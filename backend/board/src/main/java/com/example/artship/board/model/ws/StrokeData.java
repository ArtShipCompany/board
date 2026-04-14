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
public class StrokeData {
    private String strokeId;
    private String visitorId;
    private String roomId;
    private String type;

    private List<Point> points;
    
    private String color;
    private int size;
    private double opacity;

    private String shapeType;
    
    private String textContent;
    private String fontFamily;
    private int fontSize;
    
    private long timestamp;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Point {
        private double x;
        private double y;
        private double pressure;
        private long t;
    }
}