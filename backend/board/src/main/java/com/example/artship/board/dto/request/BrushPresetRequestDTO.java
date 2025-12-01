package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BrushPresetRequestDTO {
    
    @NotNull(message = "ID пользователя обязателен")
    private Long userId;
    
    @NotBlank(message = "Название пресета обязательно")
    private String name;
    
    @NotBlank(message = "Тип кисти обязателен")
    private String brushType;
    
    @NotNull(message = "Размер обязателен")
    @Positive(message = "Размер должен быть положительным")
    private Integer size;
    
    @Positive(message = "Непрозрачность должна быть положительной")
    private Double opacity;
    
    @Positive(message = "Нажим должен быть положительным")
    private Double flow;
    
    @Positive(message = "Жесткость должна быть положительной")
    private Double hardness;
    
    @Positive(message = "Интервал должен быть положительным")
    private Double spacing;
    
    private String color;
    private String textureUrl;
    private Boolean isPublic;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getBrushType() { return brushType; }
    public void setBrushType(String brushType) { this.brushType = brushType; }
    
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    
    public Double getOpacity() { return opacity; }
    public void setOpacity(Double opacity) { this.opacity = opacity; }
    
    public Double getFlow() { return flow; }
    public void setFlow(Double flow) { this.flow = flow; }
    
    public Double getHardness() { return hardness; }
    public void setHardness(Double hardness) { this.hardness = hardness; }
    
    public Double getSpacing() { return spacing; }
    public void setSpacing(Double spacing) { this.spacing = spacing; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getTextureUrl() { return textureUrl; }
    public void setTextureUrl(String textureUrl) { this.textureUrl = textureUrl; }
    
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
}