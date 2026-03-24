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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.artship.board.dto.request.FontRequestDTO;
import com.example.artship.board.dto.response.FontResponseDTO;
import com.example.artship.board.service.FontService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/fonts")
@Tag(name = "Font Controller", description = "API для управления шрифтами")
public class FontController {

    @Autowired
    private FontService fontService;

    @Operation(summary = "Получить все шрифты", description = "Возвращает список всех шрифтов")
    @ApiResponse(responseCode = "200", description = "Успешное получение")
    @GetMapping
    public ResponseEntity<List<FontResponseDTO>> getAllFonts() {
        List<FontResponseDTO> fonts = fontService.getAllFonts();
        return ResponseEntity.ok(fonts);
    }

    @Operation(summary = "Получить активные шрифты", description = "Возвращает только активные шрифты")
    @GetMapping("/active")
    public ResponseEntity<List<FontResponseDTO>> getActiveFonts() {
        List<FontResponseDTO> fonts = fontService.getActiveFonts();
        return ResponseEntity.ok(fonts);
    }

    @Operation(summary = "Получить системные шрифты", description = "Возвращает системные шрифты")
    @GetMapping("/system")
    public ResponseEntity<List<FontResponseDTO>> getSystemFonts() {
        List<FontResponseDTO> fonts = fontService.getSystemFonts();
        return ResponseEntity.ok(fonts);
    }

    @Operation(summary = "Получить шрифты по семейству", description = "Возвращает шрифты указанного семейства")
    @GetMapping("/family")
    public ResponseEntity<List<FontResponseDTO>> getFontsByFamily(
            @Parameter(description = "Название семейства", required = true)
            @RequestParam String family) {
        
        List<FontResponseDTO> fonts = fontService.getFontsByFamily(family);
        return ResponseEntity.ok(fonts);
    }

    @Operation(summary = "Получить шрифт по ID", description = "Возвращает шрифт по идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Шрифт найден"),
        @ApiResponse(responseCode = "404", description = "Шрифт не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FontResponseDTO> getFontById(
            @Parameter(description = "ID шрифта", required = true)
            @PathVariable Long id) {
        
        Optional<FontResponseDTO> font = fontService.getFontById(id);
        return font.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать шрифт", description = "Создает новый шрифт")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Шрифт создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping
    public ResponseEntity<FontResponseDTO> createFont(
            @Parameter(description = "Данные шрифта", required = true)
            @Valid @RequestBody FontRequestDTO dto) {
        
        FontResponseDTO savedFont = fontService.createFont(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFont);
    }

    @Operation(summary = "Обновить шрифт", description = "Обновляет существующий шрифт")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Шрифт обновлен"),
        @ApiResponse(responseCode = "404", description = "Шрифт не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FontResponseDTO> updateFont(
            @Parameter(description = "ID шрифта", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные", required = true)
            @Valid @RequestBody FontRequestDTO dto) {
        
        Optional<FontResponseDTO> updatedFont = fontService.updateFont(id, dto);
        return updatedFont.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Переключить активность", description = "Включает/выключает шрифт")
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Boolean> toggleFontActive(
            @Parameter(description = "ID шрифта", required = true)
            @PathVariable Long id) {
        
        boolean toggled = fontService.toggleFontActive(id);
        if (toggled) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Удалить шрифт", description = "Удаляет шрифт по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Удалено"),
        @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFont(
            @Parameter(description = "ID шрифта", required = true)
            @PathVariable Long id) {
        
        boolean deleted = fontService.deleteFont(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}