package com.example.artship.board.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "drawing_sessions")
public class DrawingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "ID доски обязателен")
    @Column(name = "board_id")
    private Long boardId;

    @NotNull(message = "ID пользователя обязателен")
    @Column(name = "user_id")
    private Long userId;

    @NotNull(message = "ID предустановки кисти обязателен")
    @Column(name = "brush_preset_id")
    private Long brushPresetId;

    @NotNull(message = "ID слоя обязателен")
    @Column(name = "layer_id")
    private Long layerId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "strokes_count")
    private Integer strokesCount;

    @Column(name = "created_at")
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