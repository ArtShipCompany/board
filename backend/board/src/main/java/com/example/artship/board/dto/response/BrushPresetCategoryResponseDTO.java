package com.example.artship.board.dto.response;

import java.time.LocalDateTime;

public class BrushPresetCategoryResponseDTO {
    private Long id;
    private Long brushPresetId;
    private Long categoryId;
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