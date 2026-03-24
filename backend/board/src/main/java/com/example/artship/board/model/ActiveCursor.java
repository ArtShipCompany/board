package com.example.artship.board.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "active_cursors")
public class ActiveCursor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "ID комнаты обязательно")
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @NotNull(message = "ID пользователя обязательно")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Координата X обязательна")
    @Column(name = "x_coord", nullable = false, precision = 10, scale = 2)
    private BigDecimal xCoord;

    @NotNull(message = "Координата Y обязательна")
    @Column(name = "y_coord", nullable = false, precision = 10, scale = 2)
    private BigDecimal yCoord;

    @NotNull(message = "Цвет обязателен")
    @Column(name = "color", nullable = false)
    private String color;

    @NotNull(message = "Отображаемое имя обязательно")
    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    public ActiveCursor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getXCoord() {
        return xCoord;
    }

    public void setXCoord(BigDecimal xCoord) {
        this.xCoord = xCoord;
    }

    public BigDecimal getYCoord() {
        return yCoord;
    }

    public void setYCoord(BigDecimal yCoord) {
        this.yCoord = yCoord;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}