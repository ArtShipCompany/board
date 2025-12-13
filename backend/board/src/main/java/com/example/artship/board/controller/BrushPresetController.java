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

import com.example.artship.board.dto.request.BrushPresetRequestDTO;
import com.example.artship.board.dto.response.BrushPresetResponseDTO;
import com.example.artship.board.service.BrushPresetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/brush-presets")
@Tag(name = "Brush Preset Controller", description = "API для управления пресетами кистей")
public class BrushPresetController {

    @Autowired
    private BrushPresetService brushPresetService;

    @Operation(summary = "Получить все пресеты кистей", 
               description = "Возвращает список всех пресетов кистей (публичных и приватных)")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка пресетов")
    @GetMapping
    public ResponseEntity<List<BrushPresetResponseDTO>> getAllPresets() {
        List<BrushPresetResponseDTO> presets = brushPresetService.getAllPresets();
        return ResponseEntity.ok(presets);
    }

    @Operation(summary = "Получить пресет по ID", 
               description = "Возвращает пресет кисти по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пресет найден"),
        @ApiResponse(responseCode = "404", description = "Пресет не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BrushPresetResponseDTO> getPresetById(
            @Parameter(description = "ID пресета", example = "1", required = true)
            @PathVariable Long id) {
        Optional<BrushPresetResponseDTO> preset = brushPresetService.getPresetById(id);
        return preset.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить пресеты пользователя", 
               description = "Возвращает все пресеты кистей, созданные указанным пользователем")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пресеты найдены"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BrushPresetResponseDTO>> getPresetsByUserId(
            @Parameter(description = "ID пользователя", example = "123", required = true)
            @PathVariable Long userId) {
        List<BrushPresetResponseDTO> presets = brushPresetService.getPresetsByUserId(userId);
        return ResponseEntity.ok(presets);
    }

    @Operation(summary = "Получить публичные пресеты", 
               description = "Возвращает список публично доступных пресетов кистей")
    @ApiResponse(responseCode = "200", description = "Успешное получение публичных пресетов")
    @GetMapping("/public")
    public ResponseEntity<List<BrushPresetResponseDTO>> getPublicPresets() {
        List<BrushPresetResponseDTO> presets = brushPresetService.getPublicPresets();
        return ResponseEntity.ok(presets);
    }

    @Operation(summary = "Создать новый пресет", 
               description = "Создает новый пресет кисти с указанными параметрами")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Пресет успешно создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping
    public ResponseEntity<BrushPresetResponseDTO> createPreset(
            @Parameter(description = "Данные для создания пресета", required = true)
            @Valid @RequestBody BrushPresetRequestDTO presetDTO) {
        BrushPresetResponseDTO savedPreset = brushPresetService.createPreset(presetDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPreset);
    }

    @Operation(summary = "Обновить пресет", 
               description = "Обновляет существующий пресет кисти")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пресет успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Пресет не найден"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен (не владелец)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BrushPresetResponseDTO> updatePreset(
            @Parameter(description = "ID пресета", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные пресета", required = true)
            @Valid @RequestBody BrushPresetRequestDTO presetDTO) {
        Optional<BrushPresetResponseDTO> updatedPreset = brushPresetService.updatePreset(id, presetDTO);
        return updatedPreset.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить пресет", 
               description = "Удаляет пресет кисти по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Пресет успешно удален"),
        @ApiResponse(responseCode = "404", description = "Пресет не найден"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен (не владелец)")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreset(
            @Parameter(description = "ID пресета", example = "1", required = true)
            @PathVariable Long id) {
        boolean deleted = brushPresetService.deletePreset(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}