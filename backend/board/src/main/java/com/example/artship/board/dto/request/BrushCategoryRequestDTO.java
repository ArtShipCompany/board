package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class BrushCategoryRequestDTO {
    
    @NotBlank(message = "Название категории обязательно")
    private String name;
    
    private String description;
    
    @NotNull(message = "Порядковый индекс обязателен")
    @PositiveOrZero(message = "Порядковый индекс не может быть отрицательным")
    private Integer orderIndex;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}