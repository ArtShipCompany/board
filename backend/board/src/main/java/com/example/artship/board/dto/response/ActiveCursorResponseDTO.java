package com.example.artship.board.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ActiveCursorResponseDTO {
    private Long id;
    private Long roomId;
    private Long userId;
    private BigDecimal xCoord;
    private BigDecimal yCoord;
    private String color;
    private String displayName;
    private LocalDateTime lastUpdate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
}