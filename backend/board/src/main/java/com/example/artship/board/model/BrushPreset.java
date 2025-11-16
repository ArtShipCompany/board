package com.example.artship.board.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Table(name = "brush_presets")
public class BrushPreset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "ID пользователя обязателен")
    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = "Название пресета обязательно")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Тип кисти обязателен")
    @Column(name = "brush_type")
    private String brushType;

    @NotNull(message = "Размер обязателен")
    @Positive(message = "Размер должен быть положительным")
    @Column(name = "size")
    private Integer size;

    @Column(name = "opacity")
    private Double opacity;

    @Column(name = "flow")
    private Double flow;

    @Column(name = "hardness")
    private Double hardness;

    @Column(name = "spacing")
    private Double spacing;

    @Column(name = "color")
    private String color;

    @Column(name = "texture_url")
    private String textureUrl;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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