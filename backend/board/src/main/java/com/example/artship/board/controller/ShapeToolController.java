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

import com.example.artship.board.dto.request.ShapeToolRequestDTO;
import com.example.artship.board.dto.response.ShapeToolResponseDTO;
import com.example.artship.board.service.ShapeToolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shape-tools")
@Tag(name = "Shape Tool Controller", description = "API для управления инструментами фигур")
public class ShapeToolController {

    @Autowired
    private ShapeToolService shapeToolService;

    @Operation(summary = "Получить все инструменты", description = "Возвращает список всех инструментов фигур")
    @ApiResponse(responseCode = "200", description = "Успешное получение")
    @GetMapping
    public ResponseEntity<List<ShapeToolResponseDTO>> getAllShapeTools() {
        List<ShapeToolResponseDTO> shapeTools = shapeToolService.getAllShapeTools();
        return ResponseEntity.ok(shapeTools);
    }

    @Operation(summary = "Получить активные инструменты", description = "Возвращает только активные инструменты")
    @GetMapping("/active")
    public ResponseEntity<List<ShapeToolResponseDTO>> getActiveShapeTools() {
        List<ShapeToolResponseDTO> shapeTools = shapeToolService.getActiveShapeTools();
        return ResponseEntity.ok(shapeTools);
    }

    @Operation(summary = "Получить инструмент по ID", description = "Возвращает инструмент по идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Инструмент найден"),
        @ApiResponse(responseCode = "404", description = "Инструмент не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ShapeToolResponseDTO> getShapeToolById(
            @Parameter(description = "ID инструмента", required = true)
            @PathVariable Long id) {
        
        Optional<ShapeToolResponseDTO> shapeTool = shapeToolService.getShapeToolById(id);
        return shapeTool.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать инструмент", description = "Создает новый инструмент фигур")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Инструмент создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping
    public ResponseEntity<ShapeToolResponseDTO> createShapeTool(
            @Parameter(description = "Данные инструмента", required = true)
            @Valid @RequestBody ShapeToolRequestDTO dto) {
        
        ShapeToolResponseDTO savedShapeTool = shapeToolService.createShapeTool(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShapeTool);
    }

    @Operation(summary = "Обновить инструмент", description = "Обновляет существующий инструмент")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Инструмент обновлен"),
        @ApiResponse(responseCode = "404", description = "Инструмент не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ShapeToolResponseDTO> updateShapeTool(
            @Parameter(description = "ID инструмента", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные", required = true)
            @Valid @RequestBody ShapeToolRequestDTO dto) {
        
        Optional<ShapeToolResponseDTO> updatedShapeTool = shapeToolService.updateShapeTool(id, dto);
        return updatedShapeTool.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Переключить активность", description = "Включает/выключает инструмент")
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Boolean> toggleShapeToolActive(
            @Parameter(description = "ID инструмента", required = true)
            @PathVariable Long id) {
        
        boolean toggled = shapeToolService.toggleShapeToolActive(id);
        if (toggled) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Удалить инструмент", description = "Удаляет инструмент по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Удалено"),
        @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShapeTool(
            @Parameter(description = "ID инструмента", required = true)
            @PathVariable Long id) {
        
        boolean deleted = shapeToolService.deleteShapeTool(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}