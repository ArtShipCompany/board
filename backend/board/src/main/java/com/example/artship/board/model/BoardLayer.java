package com.example.artship.board.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Entity
@Table(name = "board_layers")
public class BoardLayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "ID доски обязателен")
    @Column(name = "board_id")
    private Long boardId;

    @NotNull(message = "Название слоя обязательно")
    @Column(name = "name")
    private String name;

    @NotNull(message = "Порядковый индекс обязателен")
    @PositiveOrZero(message = "Порядковый индекс не может быть отрицательным")
    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "opacity")
    private Double opacity;

    @Column(name = "is_visible")
    private Boolean isVisible;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Геттеры и сеттеры

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}