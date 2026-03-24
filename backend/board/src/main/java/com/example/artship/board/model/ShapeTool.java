package com.example.artship.board.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "shape_tools")
public class ShapeTool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Название обязательно")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Отображаемое имя обязательно")
    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "min_size")
    private Integer minSize;

    @Column(name = "max_size")
    private Integer maxSize;

    @Column(name = "can_resize")
    private Boolean canResize;

    @Column(name = "can_rotate")
    private Boolean canRotate;

    @Column(name = "can_change_color")
    private Boolean canChangeColor;

    @Column(name = "can_change_opacity")
    private Boolean canChangeOpacity;

    @Column(name = "default_color")
    private String defaultColor;

    @Column(name = "default_opacity", precision = 3, scale = 2)
    private BigDecimal defaultOpacity;

    @Column(name = "parameters_schema", columnDefinition = "TEXT")
    private String parametersSchema;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    public ShapeTool() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Integer getMinSize() {
        return minSize;
    }

    public void setMinSize(Integer minSize) {
        this.minSize = minSize;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public Boolean getCanResize() {
        return canResize;
    }

    public void setCanResize(Boolean canResize) {
        this.canResize = canResize;
    }

    public Boolean getCanRotate() {
        return canRotate;
    }

    public void setCanRotate(Boolean canRotate) {
        this.canRotate = canRotate;
    }

    public Boolean getCanChangeColor() {
        return canChangeColor;
    }

    public void setCanChangeColor(Boolean canChangeColor) {
        this.canChangeColor = canChangeColor;
    }

    public Boolean getCanChangeOpacity() {
        return canChangeOpacity;
    }

    public void setCanChangeOpacity(Boolean canChangeOpacity) {
        this.canChangeOpacity = canChangeOpacity;
    }

    public String getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    public BigDecimal getDefaultOpacity() {
        return defaultOpacity;
    }

    public void setDefaultOpacity(BigDecimal defaultOpacity) {
        this.defaultOpacity = defaultOpacity;
    }

    public String getParametersSchema() {
        return parametersSchema;
    }

    public void setParametersSchema(String parametersSchema) {
        this.parametersSchema = parametersSchema;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}