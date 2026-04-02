package com.example.artship.board.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShapeToolResponseDTO {
    private Long id;
    private String name;
    private String displayName;
    private String iconUrl;
    private Integer minSize;
    private Integer maxSize;
    private Boolean canResize;
    private Boolean canRotate;
    private Boolean canChangeColor;
    private Boolean canChangeOpacity;
    private String defaultColor;
    private BigDecimal defaultOpacity;
    private String parametersSchema;
    private Boolean isActive;
    private Integer orderIndex;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    
    public Integer getMinSize() { return minSize; }
    public void setMinSize(Integer minSize) { this.minSize = minSize; }
    
    public Integer getMaxSize() { return maxSize; }
    public void setMaxSize(Integer maxSize) { this.maxSize = maxSize; }
    
    public Boolean getCanResize() { return canResize; }
    public void setCanResize(Boolean canResize) { this.canResize = canResize; }
    
    public Boolean getCanRotate() { return canRotate; }
    public void setCanRotate(Boolean canRotate) { this.canRotate = canRotate; }
    
    public Boolean getCanChangeColor() { return canChangeColor; }
    public void setCanChangeColor(Boolean canChangeColor) { this.canChangeColor = canChangeColor; }
    
    public Boolean getCanChangeOpacity() { return canChangeOpacity; }
    public void setCanChangeOpacity(Boolean canChangeOpacity) { this.canChangeOpacity = canChangeOpacity; }
    
    public String getDefaultColor() { return defaultColor; }
    public void setDefaultColor(String defaultColor) { this.defaultColor = defaultColor; }
    
    public BigDecimal getDefaultOpacity() { return defaultOpacity; }
    public void setDefaultOpacity(BigDecimal defaultOpacity) { this.defaultOpacity = defaultOpacity; }
    
    public String getParametersSchema() { return parametersSchema; }
    public void setParametersSchema(String parametersSchema) { this.parametersSchema = parametersSchema; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}