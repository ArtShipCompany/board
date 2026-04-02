package com.example.artship.board.dto.response;

import java.time.LocalDateTime;

public class ActionHistoryResponseDTO {
    private Long id;
    private Long boardId;
    private Long userId;
    private String actionType;
    private String targetType;
    private Long targetId;
    private String previousData;
    private String newData;
    private LocalDateTime timestamp;
    private Long sessionId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
}