package com.example.artship.board.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ActiveCursorRequestDTO {
    
    @NotNull(message = "ID комнаты обязательно")
    private Long roomId;
    
    @NotNull(message = "ID пользователя обязательно")
    private Long userId;
    
    @NotNull(message = "Координата X обязательна")
    @DecimalMin(value = "-10000", message = "Координата X слишком маленькая")
    @DecimalMax(value = "10000", message = "Координата X слишком большая")
    private BigDecimal xCoord;
    
    @NotNull(message = "Координата Y обязательна")
    @DecimalMin(value = "-10000", message = "Координата Y слишком маленькая")
    @DecimalMax(value = "10000", message = "Координата Y слишком большая")
    private BigDecimal yCoord;
    
    @NotBlank(message = "Цвет обязателен")
    @Size(max = 7, message = "Цвет должен быть в формате #RRGGBB")
    private String color;
    
    @NotBlank(message = "Отображаемое имя обязательно")
    @Size(max = 50, message = "Имя не должно превышать 50 символов")
    private String displayName;

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public BigDecimal getXCoord() { return xCoord; }
    public void setXCoord(BigDecimal xCoord) { this.xCoord = xCoord; }
    
    public BigDecimal getYCoord() { return yCoord; }
    public void setYCoord(BigDecimal yCoord) { this.yCoord = yCoord; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}