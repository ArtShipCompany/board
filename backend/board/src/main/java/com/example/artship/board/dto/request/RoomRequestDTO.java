package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class RoomRequestDTO {
    
    @NotNull(message = "ID создателя обязателен")
    private Long creatorId;
    
    @NotBlank(message = "Название комнаты обязательно")
    private String title;
    
    @NotNull(message = "Максимальное количество людей обязательно")
    @PositiveOrZero(message = "Максимальное количество людей не может быть отрицательным")
    private Integer maxPeople;
    
    private String password;

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Integer getMaxPeople() { return maxPeople; }
    public void setMaxPeople(Integer maxPeople) { this.maxPeople = maxPeople; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}