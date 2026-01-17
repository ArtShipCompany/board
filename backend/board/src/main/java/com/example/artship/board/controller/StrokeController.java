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

import com.example.artship.board.dto.request.StrokeRequestDTO;
import com.example.artship.board.dto.response.StrokeResponseDTO;
import com.example.artship.board.service.StrokeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/strokes")
@Tag(name = "Stroke Controller", description = "API для управления штрихами/мазками кисти при рисовании")
public class StrokeController {

    @Autowired
    private StrokeService strokeService;

    @Operation(summary = "Получить все штрихи", 
               description = "Возвращает список всех штрихов/мазков кисти")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка штрихов")
    @GetMapping
    public ResponseEntity<List<StrokeResponseDTO>> getAllStrokes() {
        List<StrokeResponseDTO> strokes = strokeService.getAllStrokes();
        return ResponseEntity.ok(strokes);
    }

    @Operation(summary = "Получить штрих по ID", 
               description = "Возвращает штрих/мазок кисти по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Штрих найден"),
        @ApiResponse(responseCode = "404", description = "Штрих не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StrokeResponseDTO> getStrokeById(
            @Parameter(description = "ID штриха", example = "1", required = true)
            @PathVariable Long id) {
        Optional<StrokeResponseDTO> stroke = strokeService.getStrokeById(id);
        return stroke.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить штрихи по ID сессии", 
               description = "Возвращает все штрихи, созданные в указанной сессии рисования")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Штрихи найдены"),
        @ApiResponse(responseCode = "404", description = "Сессия не найдена")
    })
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<StrokeResponseDTO>> getStrokesBySessionId(
            @Parameter(description = "ID сессии рисования", example = "1", required = true)
            @PathVariable Long sessionId) {
        List<StrokeResponseDTO> strokes = strokeService.getStrokesBySessionId(sessionId);
        return ResponseEntity.ok(strokes);
    }

    @Operation(summary = "Получить штрихи по ID слоя", 
               description = "Возвращает все штрихи, принадлежащие указанному слою")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Штрихи найдены"),
        @ApiResponse(responseCode = "404", description = "Слой не найдена")
    })
    @GetMapping("/layer/{layerId}")
    public ResponseEntity<List<StrokeResponseDTO>> getStrokesByLayerId(
            @Parameter(description = "ID слоя", example = "1", required = true)
            @PathVariable Long layerId) {
        List<StrokeResponseDTO> strokes = strokeService.getStrokesByLayerId(layerId);
        return ResponseEntity.ok(strokes);
    }

    @Operation(summary = "Получить штрихи по ID пресета кисти", 
               description = "Возвращает все штрихи, созданные с использованием указанного пресета кисти")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Штрихи найдены"),
        @ApiResponse(responseCode = "404", description = "Пресет кисти не найден")
    })
    @GetMapping("/brush-preset/{brushPresetId}")
    public ResponseEntity<List<StrokeResponseDTO>> getStrokesByBrushPresetId(
            @Parameter(description = "ID пресета кисти", example = "1", required = true)
            @PathVariable Long brushPresetId) {
        List<StrokeResponseDTO> strokes = strokeService.getStrokesByBrushPresetId(brushPresetId);
        return ResponseEntity.ok(strokes);
    }

    @Operation(summary = "Создать новый штрих", 
               description = "Создает новый штрих/мазок кисти во время рисования")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Штрих успешно создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
        @ApiResponse(responseCode = "404", description = "Сессия, слой или пресет кисти не найдены")
    })
    @PostMapping
    public ResponseEntity<StrokeResponseDTO> createStroke(
            @Parameter(description = "Данные для создания штриха", required = true)
            @Valid @RequestBody StrokeRequestDTO strokeDTO) {
        StrokeResponseDTO savedStroke = strokeService.createStroke(strokeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStroke);
    }

    @Operation(summary = "Обновить штрих", 
               description = "Обновляет существующий штрих/мазок кисти")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Штрих успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Штрих не найден"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен (не создатель штриха)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<StrokeResponseDTO> updateStroke(
            @Parameter(description = "ID штриха", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные штриха", required = true)
            @Valid @RequestBody StrokeRequestDTO strokeDTO) {
        Optional<StrokeResponseDTO> updatedStroke = strokeService.updateStroke(id, strokeDTO);
        return updatedStroke.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить штрих", 
               description = "Удаляет штрих/мазок кисти по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Штрих успешно удален"),
        @ApiResponse(responseCode = "404", description = "Штрих не найден"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен (не создатель штриха)")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStroke(
            @Parameter(description = "ID штриха", example = "1", required = true)
            @PathVariable Long id) {
        boolean deleted = strokeService.deleteStroke(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}