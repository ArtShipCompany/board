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

import com.example.artship.board.dto.request.BrushCategoryRequestDTO;
import com.example.artship.board.dto.response.BrushCategoryResponseDTO;
import com.example.artship.board.service.BrushCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/brush-categories")
@Tag(name = "Brush Category Controller", description = "API для управления категориями кистей")
public class BrushCategoryController {

    @Autowired
    private BrushCategoryService brushCategoryService;

    @Operation(summary = "Получить все категории кистей", description = "Возвращает список всех категорий кистей")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка категорий")
    @GetMapping
    public ResponseEntity<List<BrushCategoryResponseDTO>> getAllCategories() {
        List<BrushCategoryResponseDTO> categories = brushCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Получить категорию по ID", description = "Возвращает категорию кистей по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Категория найдена"),
        @ApiResponse(responseCode = "404", description = "Категория не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BrushCategoryResponseDTO> getCategoryById(
            @Parameter(description = "ID категории", example = "1", required = true)
            @PathVariable Long id) {
        Optional<BrushCategoryResponseDTO> category = brushCategoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать новую категорию", description = "Создает новую категорию кистей")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Категория успешно создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PostMapping
    public ResponseEntity<BrushCategoryResponseDTO> createCategory(
            @Parameter(description = "Данные для создания категории", required = true)
            @Valid @RequestBody BrushCategoryRequestDTO categoryDTO) {
        BrushCategoryResponseDTO savedCategory = brushCategoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @Operation(summary = "Обновить категорию", description = "Обновляет существующую категорию кистей")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Категория успешно обновлена"),
        @ApiResponse(responseCode = "404", description = "Категория не найдена"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BrushCategoryResponseDTO> updateCategory(
            @Parameter(description = "ID категории", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные категории", required = true)
            @Valid @RequestBody BrushCategoryRequestDTO categoryDTO) {
        Optional<BrushCategoryResponseDTO> updatedCategory = brushCategoryService.updateCategory(id, categoryDTO);
        return updatedCategory.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить категорию", description = "Удаляет категорию кистей по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Категория успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Категория не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID категории", example = "1", required = true)
            @PathVariable Long id) {
        boolean deleted = brushCategoryService.deleteCategory(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}