package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class BoardCollaboratorRequestDTO {
    
    @NotNull(message = "ID доски обязательно")
    private Long boardId;
    
    @NotNull(message = "ID пользователя обязательно")
    private Long userId;
    
    @NotBlank(message = "Права доступа обязательны")
    @Pattern(regexp = "^(view|edit|admin)$", message = "Права доступа должны быть: view, edit или admin")
    private String permission;

    public Long getBoardId() { return boardId; }
    public void setBoardId(Long boardId) { this.boardId = boardId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
}