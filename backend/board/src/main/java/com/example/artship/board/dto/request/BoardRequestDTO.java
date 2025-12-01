package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class BoardRequestDTO {
    
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;
    
    @NotNull(message = "Ширина обязательна")
    @PositiveOrZero(message = "Ширина не может быть отрицательной")
    private Integer width;
    
    @NotNull(message = "Высота обязательна")
    @PositiveOrZero(message = "Высота не может быть отрицательной")
    private Integer height;
    
    @Size(max = 7, message = "Цвет должен быть в формате #RRGGBB (например, #FFFFFF)")
    private String backgroundColor;
    
    @Size(max = 2048, message = "Ссылка на изображение слишком длинная")
    private String backgroundImage;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    
    public String getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }
    
    public String getBackgroundImage() { return backgroundImage; }
    public void setBackgroundImage(String backgroundImage) { this.backgroundImage = backgroundImage; }
}