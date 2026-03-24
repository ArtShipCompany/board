package com.example.artship.board.dto.response;

import java.time.LocalDateTime;

public class RoomParticipantResponseDTO {
    private Long id;
    private Long roomId;
    private Long userId;
    private String nickname;
    private String cursorColor;
    private Boolean isOnline;
    private LocalDateTime joinedAt;
    private LocalDateTime lastActiveAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getCursorColor() { return cursorColor; }
    public void setCursorColor(String cursorColor) { this.cursorColor = cursorColor; }
    
    public Boolean getIsOnline() { return isOnline; }
    public void setIsOnline(Boolean isOnline) { this.isOnline = isOnline; }
    
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    
    public LocalDateTime getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(LocalDateTime lastActiveAt) { this.lastActiveAt = lastActiveAt; }
}