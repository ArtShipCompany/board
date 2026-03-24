package com.example.artship.board.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "room_participants")
public class RoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "ID комнаты обязательно")
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @NotNull(message = "ID участника обязательно")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Никнейм обязателен")
    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "cursor_color")
    private String cursorColor;

    @Column(name = "is_online")
    private Boolean isOnline;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    public RoomParticipant() {
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCursorColor() {
        return cursorColor;
    }

    public void setCursorColor(String cursorColor) {
        this.cursorColor = cursorColor;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(LocalDateTime lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }
}