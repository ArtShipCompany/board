package com.example.artship.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class FontRequestDTO {
    
    @NotBlank(message = "Название обязательно")
    private String name;
    
    @NotBlank(message = "Семейство обязательно")
    private String family;
    
    @NotBlank(message = "URL файла обязателен")
    private String fileUrl;
    
    @Pattern(regexp = "^(woff2|woff|ttf|otf)$", message = "Неподдерживаемый формат шрифта")
    private String fileFormat;
    
    @Pattern(regexp = "^(normal|bold|light|medium|black|thin)$", message = "Неподдерживаемая толщина")
    private String weight;
    
    @Pattern(regexp = "^(normal|italic|oblique)$", message = "Неподдерживаемый стиль")
    private String style;
    
    private Boolean isSystem;
    private Boolean isActive;
    private Long uploadedBy;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getFamily() { return family; }
    public void setFamily(String family) { this.family = family; }
    
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    
    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
    
    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }
    
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    
    public Boolean getIsSystem() { return isSystem; }
    public void setIsSystem(Boolean isSystem) { this.isSystem = isSystem; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Long getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }
}