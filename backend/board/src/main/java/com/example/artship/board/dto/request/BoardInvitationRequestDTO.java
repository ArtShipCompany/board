package com.example.artship.board.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class BoardInvitationRequestDTO {
    
    @NotNull(message = "ID доски обязательно")
    private Long boardId;
    
    @Email(message = "Некорректный формат email")
    private String email;

    public Long getBoardId() { return boardId; }
    public void setBoardId(Long boardId) { this.boardId = boardId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}