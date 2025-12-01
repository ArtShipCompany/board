package com.example.artship.board.dto.response;

import java.time.LocalDateTime;

public class DrawingSessionResponseDTO {
    private Long id;
    private Long boardId;
    private Long userId;
    private Long brushPresetId;
    private Long layerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer strokesCount;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}