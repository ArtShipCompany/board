package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotNull;

public class BrushPresetCategoryRequestDTO {
    
    @NotNull(message = "ID предустановки кисти обязателен")
    private Long brushPresetId;
    
    @NotNull(message = "ID категории обязателен")
    private Long categoryId;

    public Long getBrushPresetId() { return brushPresetId; }
    public void setBrushPresetId(Long brushPresetId) { this.brushPresetId = brushPresetId; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}