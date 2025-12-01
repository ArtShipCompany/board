package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class StrokeRequestDTO {
    
    @NotNull(message = "ID сессии обязателен")
    private Long sessionId;
    
    @NotNull(message = "ID предустановки кисти обязателен")
    private Long brushPresetId;
    
    @NotNull(message = "ID слоя обязателен")
    private Long layerId;
    
    @NotBlank(message = "Цвет обязателен")
    private String color;
    
    @NotNull(message = "Размер обязателен")
    @PositiveOrZero(message = "Размер не может быть отрицательным")
    private Integer size;
    
    @PositiveOrZero(message = "Прозрачность не может быть отрицательной")
    private Double opacity;
    
    private String points;

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    
    public Long getBrushPresetId() { return brushPresetId; }
    public void setBrushPresetId(Long brushPresetId) { this.brushPresetId = brushPresetId; }
    
    public Long getLayerId() { return layerId; }
    public void setLayerId(Long layerId) { this.layerId = layerId; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    
    public Double getOpacity() { return opacity; }
    public void setOpacity(Double opacity) { this.opacity = opacity; }
    
    public String getPoints() { return points; }
    public void setPoints(String points) { this.points = points; }
}