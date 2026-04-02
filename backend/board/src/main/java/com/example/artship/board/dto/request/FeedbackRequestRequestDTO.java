package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class FeedbackRequestRequestDTO {
    
    @NotNull(message = "ID пользователя обязателен")
    private Long userId;
    
    @NotBlank(message = "Тип запроса обязателен")
    @Pattern(regexp = "^(tool|brush|shape|font|feature)$", message = "Неподдерживаемый тип запроса")
    private String requestType;
    
    @NotBlank(message = "Заголовок обязателен")
    @Size(max = 200, message = "Заголовок не должен превышать 200 символов")
    private String title;
    
    @NotBlank(message = "Описание обязательно")
    @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
    private String description;
    
    @Pattern(regexp = "^(low|normal|high)$", message = "Неподдерживаемый приоритет")
    private String priority;
    
    private String status;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}