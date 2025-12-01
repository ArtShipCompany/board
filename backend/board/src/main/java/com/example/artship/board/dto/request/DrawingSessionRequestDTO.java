package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class DrawingSessionRequestDTO {
    
    @NotNull(message = "ID доски обязателен")
    private Long boardId;
    
    @NotNull(message = "ID пользователя обязателен")
    private Long userId;
    
    @NotNull(message = "ID предустановки кисти обязателен")
    private Long brushPresetId;
    
    @NotNull(message = "ID слоя обязателен")
    private Long layerId;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    private Integer strokesCount;

    public Long getBoardId() { return boardId; }
    public void setBoardId(Long boardId) { this.boardId = boardId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getBrushPresetId() { return brushPresetId; }
    public void setBrushPresetId(Long brushPresetId) { this.brushPresetId = brushPresetId; }
    
    public Long getLayerId() { return layerId; }
    public void setLayerId(Long layerId) { this.layerId = layerId; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Integer getStrokesCount() { return strokesCount; }
    public void setStrokesCount(Integer strokesCount) { this.strokesCount = strokesCount; }
}