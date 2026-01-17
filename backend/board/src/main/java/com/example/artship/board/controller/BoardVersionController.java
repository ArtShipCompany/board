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

import com.example.artship.board.dto.request.BoardVersionRequestDTO;
import com.example.artship.board.dto.response.BoardVersionResponseDTO;
import com.example.artship.board.service.BoardVersionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/board-versions")
@Tag(name = "Board Version Controller", description = "API для управления версиями досок")
public class BoardVersionController {

    @Autowired
    private BoardVersionService boardVersionService;

    @Operation(summary = "Получить все версии", description = "Возвращает список всех версий досок")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка версий")
    @GetMapping
    public ResponseEntity<List<BoardVersionResponseDTO>> getAllVersions() {
        List<BoardVersionResponseDTO> versions = boardVersionService.getAllVersions();
        return ResponseEntity.ok(versions);
    }

    @Operation(summary = "Получить версию по ID", description = "Возвращает версию доски по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Версия найдена"),
        @ApiResponse(responseCode = "404", description = "Версия не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BoardVersionResponseDTO> getVersionById(
            @Parameter(description = "ID версии", example = "1", required = true)
            @PathVariable Long id) {
        Optional<BoardVersionResponseDTO> version = boardVersionService.getVersionById(id);
        return version.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать новую версию", description = "Создает новую версию доски")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Версия успешно создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PostMapping
    public ResponseEntity<BoardVersionResponseDTO> createVersion(
            @Parameter(description = "Данные для создания версии", required = true)
            @Valid @RequestBody BoardVersionRequestDTO versionDTO) {
        BoardVersionResponseDTO savedVersion = boardVersionService.createVersion(versionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVersion);
    }

    @Operation(summary = "Обновить версию", description = "Обновляет существующую версию доски")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Версия успешно обновлена"),
        @ApiResponse(responseCode = "404", description = "Версия не найдена"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BoardVersionResponseDTO> updateVersion(
            @Parameter(description = "ID версии", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные версии", required = true)
            @Valid @RequestBody BoardVersionRequestDTO versionDTO) {
        Optional<BoardVersionResponseDTO> updatedVersion = boardVersionService.updateVersion(id, versionDTO);
        return updatedVersion.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить версию", description = "Удаляет версию доски по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Версия успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Версия не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVersion(
            @Parameter(description = "ID версии", example = "1", required = true)
            @PathVariable Long id) {
        boolean deleted = boardVersionService.deleteVersion(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}