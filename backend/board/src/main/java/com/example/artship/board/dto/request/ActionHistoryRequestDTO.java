package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotNull;

public class ActionHistoryRequestDTO {
    
    @NotNull(message = "ID доски обязательно")
    private Long boardId;
    
    @NotNull(message = "ID пользователя обязательно")
    private Long userId;
    
    @NotNull(message = "Тип действия обязателен")
    private String actionType;
    
    @NotNull(message = "Тип цели обязателен")
    private String targetType;
    
    @NotNull(message = "ID цели обязателен")
    private Long targetId;
    
    private String previousData;
    private String newData;
    
    private Long sessionId;

    public Long getBoardId() { return boardId; }
    public void setBoardId(Long boardId) { this.boardId = boardId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    
    public String getPreviousData() { return previousData; }
    public void setPreviousData(String previousData) { this.previousData = previousData; }
    
    public String getNewData() { return newData; }
    public void setNewData(String newData) { this.newData = newData; }
    
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
}