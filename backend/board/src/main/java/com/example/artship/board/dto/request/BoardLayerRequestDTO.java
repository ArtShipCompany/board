package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class BoardLayerRequestDTO {
    
    @NotNull(message = "ID доски обязателен")
    private Long boardId;
    
    @NotBlank(message = "Название слоя обязательно")
    private String name;
    
    @NotNull(message = "Порядковый индекс обязателен")
    @PositiveOrZero(message = "Порядковый индекс не может быть отрицательным")
    private Integer orderIndex;
    
    @PositiveOrZero(message = "Прозрачность не может быть отрицательной")
    private Double opacity;
    
    private Boolean isVisible;

    public Long getBoardId() { return boardId; }
    public void setBoardId(Long boardId) { this.boardId = boardId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    
    public Double getOpacity() { return opacity; }
    public void setOpacity(Double opacity) { this.opacity = opacity; }
    
    public Boolean getIsVisible() { return isVisible; }
    public void setIsVisible(Boolean isVisible) { this.isVisible = isVisible; }
}