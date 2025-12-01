package com.example.artship.board.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequestDTO {
    
    @NotBlank(message = "Никнейм обязателен")
    @Size(max = 50, message = "Никнейм не должен превышать 50 символов")
    private String nickname;
    
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    @Size(max = 100, message = "Email не должен превышать 100 символов")
    private String email;
    
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, max = 100, message = "Пароль должен быть от 6 до 100 символов")
    private String password;

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}