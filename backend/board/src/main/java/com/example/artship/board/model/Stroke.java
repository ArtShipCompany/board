package com.example.artship.board.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Entity
@Table(name = "strokes")
public class Stroke {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "ID сессии обязателен")
    @Column(name = "session_id")
    private Long sessionId;

    @NotNull(message = "ID предустановки кисти обязателен")
    @Column(name = "brush_preset_id")
    private Long brushPresetId;

    @NotNull(message = "ID слоя обязателен")
    @Column(name = "layer_id")
    private Long layerId;

    @NotBlank(message = "Цвет обязателен")
    @Column(name = "color")
    private String color;

    @NotNull(message = "Размер обязателен")
    @PositiveOrZero(message = "Размер не может быть отрицательным")
    @Column(name = "size")
    private Integer size;

    @Column(name = "opacity")
    private Double opacity;

    @Column(name = "points")
    private String points;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}