package com.example.artship.board.dto.response;

import java.time.LocalDateTime;

public class BrushPresetResponseDTO {
    private Long id;
    private Long userId;
    private String name;
    private String brushType;
    private Integer size;
    private Double opacity;
    private Double flow;
    private Double hardness;
    private Double spacing;
    private String color;
    private String textureUrl;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}