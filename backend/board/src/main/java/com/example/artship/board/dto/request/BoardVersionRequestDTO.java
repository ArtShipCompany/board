package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotNull;

public class BoardVersionRequestDTO {
    
    @NotNull(message = "ID доски обязателен")
    private Long boardId;
    
    @NotNull(message = "ID пользователя обязателен")
    private Long userId;
    
    @NotNull(message = "Номер версии обязателен")
    private Integer versionNumber;
    
    private String description;
    private String snapshotData;

    public Long getBoardId() { return boardId; }
    public void setBoardId(Long boardId) { this.boardId = boardId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSnapshotData() { return snapshotData; }
    public void setSnapshotData(String snapshotData) { this.snapshotData = snapshotData; }
}