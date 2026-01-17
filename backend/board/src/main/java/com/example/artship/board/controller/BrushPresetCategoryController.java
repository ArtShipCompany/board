package com.example.artship.board.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.artship.board.dto.request.BrushPresetCategoryRequestDTO;
import com.example.artship.board.dto.response.BrushPresetCategoryResponseDTO;
import com.example.artship.board.service.BrushPresetCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/brush-preset-categories")
@Tag(name = "Brush Preset Category Controller", description = "API для управления связями пресетов кистей и категорий")
public class BrushPresetCategoryController {

    @Autowired
    private BrushPresetCategoryService brushPresetCategoryService;

    @Operation(summary = "Получить все связи пресетов и категорий", 
               description = "Возвращает список всех связей между пресетами кистей и категориями")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка связей")
    @GetMapping
    public ResponseEntity<List<BrushPresetCategoryResponseDTO>> getAllLinks() {
        List<BrushPresetCategoryResponseDTO> links = brushPresetCategoryService.getAllLinks();
        return ResponseEntity.ok(links);
    }

    @Operation(summary = "Получить связь по ID", 
               description = "Возвращает связь между пресетом кисти и категорией по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Связь найдена"),
        @ApiResponse(responseCode = "404", description = "Связь не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BrushPresetCategoryResponseDTO> getLinkById(
            @Parameter(description = "ID связи", example = "1", required = true)
            @PathVariable Long id) {
        Optional<BrushPresetCategoryResponseDTO> link = brushPresetCategoryService.getLinkById(id);
        return link.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать новую связь", 
               description = "Создает новую связь между пресетом кисти и категорией")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Связь успешно создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
        @ApiResponse(responseCode = "404", description = "Пресет или категория не найдены")
    })
    @PostMapping
    public ResponseEntity<BrushPresetCategoryResponseDTO> createLink(
            @Parameter(description = "Данные для создания связи", required = true)
            @Valid @RequestBody BrushPresetCategoryRequestDTO linkDTO) {
        BrushPresetCategoryResponseDTO savedLink = brushPresetCategoryService.createLink(linkDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLink);
    }

    @Operation(summary = "Обновить связь", 
               description = "Обновляет существующую связь между пресетом кисти и категорией")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Связь успешно обновлена"),
        @ApiResponse(responseCode = "404", description = "Связь не найдена"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BrushPresetCategoryResponseDTO> updateLink(
            @Parameter(description = "ID связи", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные связи", required = true)
            @Valid @RequestBody BrushPresetCategoryRequestDTO linkDTO) {
        Optional<BrushPresetCategoryResponseDTO> updatedLink = brushPresetCategoryService.updateLink(id, linkDTO);
        return updatedLink.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить связь", 
               description = "Удаляет связь между пресетом кисти и категорией по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Связь успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Связь не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLink(
            @Parameter(description = "ID связи", example = "1", required = true)
            @PathVariable Long id) {
        boolean deleted = brushPresetCategoryService.deleteLink(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}