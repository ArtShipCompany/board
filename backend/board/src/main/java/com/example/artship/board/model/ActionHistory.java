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
@Table(name = "action_history")
public class ActionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "ID доски обязательно")
    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @NotNull(message = "ID пользователя обязательно")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Тип действия обязателен")
    @Column(name = "action_type", nullable = false)
    private String actionType;

    @NotNull(message = "Тип цели обязателен")
    @Column(name = "target_type", nullable = false)
    private String targetType;

    @NotNull(message = "ID цели обязателен")
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "previous_data", columnDefinition = "TEXT")
    private String previousData;

    @Column(name = "new_data", columnDefinition = "TEXT")
    private String newData;

    @NotNull(message = "Временная метка обязательна")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "session_id")
    private Long sessionId;

    public ActionHistory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getPreviousData() {
        return previousData;
    }

    public void setPreviousData(String previousData) {
        this.previousData = previousData;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}