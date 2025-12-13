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

import com.example.artship.board.dto.request.BoardLayerRequestDTO;
import com.example.artship.board.dto.response.BoardLayerResponseDTO;
import com.example.artship.board.service.BoardLayerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/board-layers")
@Tag(name = "Board Layer Controller", description = "API для управления слоями досок")
public class BoardLayerController {

    @Autowired
    private BoardLayerService boardLayerService;

    @Operation(summary = "Получить все слои", description = "Возвращает список всех слоев")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка слоев")
    @GetMapping
    public ResponseEntity<List<BoardLayerResponseDTO>> getAllLayers() {
        List<BoardLayerResponseDTO> layers = boardLayerService.getAllLayers();
        return ResponseEntity.ok(layers);
    }

    @Operation(summary = "Получить слой по ID", description = "Возвращает слой по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Слой найден"),
        @ApiResponse(responseCode = "404", description = "Слой не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BoardLayerResponseDTO> getLayerById(
            @Parameter(description = "ID слоя", example = "1", required = true)
            @PathVariable Long id) {
        Optional<BoardLayerResponseDTO> layer = boardLayerService.getLayerById(id);
        return layer.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить слои по ID доски", description = "Возвращает все слои для указанной доски")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Слои найдены"),
        @ApiResponse(responseCode = "404", description = "Доска не найдена")
    })
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<BoardLayerResponseDTO>> getLayersByBoardId(
            @Parameter(description = "ID доски", example = "1", required = true)
            @PathVariable Long boardId) {
        List<BoardLayerResponseDTO> layers = boardLayerService.getLayersByBoardId(boardId);
        return ResponseEntity.ok(layers);
    }

    @Operation(summary = "Создать новый слой", description = "Создает новый слой для доски")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Слой успешно создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
        @ApiResponse(responseCode = "404", description = "Доска не найдена")
    })
    @PostMapping
    public ResponseEntity<BoardLayerResponseDTO> createLayer(
            @Parameter(description = "Данные для создания слоя", required = true)
            @Valid @RequestBody BoardLayerRequestDTO layerDTO) {
        BoardLayerResponseDTO savedLayer = boardLayerService.createLayer(layerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLayer);
    }

    @Operation(summary = "Обновить слой", description = "Обновляет существующий слой")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Слой успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Слой не найден"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BoardLayerResponseDTO> updateLayer(
            @Parameter(description = "ID слоя", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные слоя", required = true)
            @Valid @RequestBody BoardLayerRequestDTO layerDTO) {
        Optional<BoardLayerResponseDTO> updatedLayer = boardLayerService.updateLayer(id, layerDTO);
        return updatedLayer.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить слой", description = "Удаляет слой по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Слой успешно удален"),
        @ApiResponse(responseCode = "404", description = "Слой не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLayer(
            @Parameter(description = "ID слоя", example = "1", required = true)
            @PathVariable Long id) {
        boolean deleted = boardLayerService.deleteLayer(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}