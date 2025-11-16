package com.example.artship.board.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "brush_preset_categories")
public class BrushPresetCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "ID предустановки кисти обязателен")
    @Column(name = "brush_preset_id")
    private Long brushPresetId;

    @NotNull(message = "ID категории обязателен")
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBrushPresetId() { return brushPresetId; }
    public void setBrushPresetId(Long brushPresetId) { this.brushPresetId = brushPresetId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}