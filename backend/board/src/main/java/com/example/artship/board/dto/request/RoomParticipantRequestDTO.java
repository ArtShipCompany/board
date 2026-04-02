package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RoomParticipantRequestDTO {
    
    @NotNull(message = "ID комнаты обязательно")
    private Long roomId;
    
    @NotNull(message = "ID участника обязательно")
    private Long userId;
    
    @NotBlank(message = "Никнейм обязателен")
    @Size(max = 50, message = "Никнейм не должен превышать 50 символов")
    private String nickname;
    
    @Size(max = 7, message = "Цвет должен быть в формате #RRGGBB")
    private String cursorColor;
    
    private Boolean isOnline;

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
}