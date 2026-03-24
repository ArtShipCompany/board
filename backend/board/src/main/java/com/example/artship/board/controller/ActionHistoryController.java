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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.artship.board.dto.request.ActionHistoryRequestDTO;
import com.example.artship.board.dto.response.ActionHistoryResponseDTO;
import com.example.artship.board.service.ActionHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/action-history")
@Tag(name = "Action History Controller", description = "API для истории действий")
public class ActionHistoryController {

    @Autowired
    private ActionHistoryService actionHistoryService;

    @Operation(summary = "Получить всю историю", description = "Возвращает все записи истории действий")
    @ApiResponse(responseCode = "200", description = "Успешное получение")
    @GetMapping
    public ResponseEntity<List<ActionHistoryResponseDTO>> getAllHistory() {
        List<ActionHistoryResponseDTO> history = actionHistoryService.getAllHistory();
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Получить запись по ID", description = "Возвращает запись по идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Запись найдена"),
        @ApiResponse(responseCode = "404", description = "Запись не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ActionHistoryResponseDTO> getHistoryById(
            @Parameter(description = "ID записи", required = true)
            @PathVariable Long id) {
        
        Optional<ActionHistoryResponseDTO> history = actionHistoryService.getHistoryById(id);
        return history.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить историю доски", description = "Возвращает историю действий для указанной доски")
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<ActionHistoryResponseDTO>> getHistoryByBoardId(
            @Parameter(description = "ID доски", required = true)
            @PathVariable Long boardId) {
        
        List<ActionHistoryResponseDTO> history = actionHistoryService.getHistoryByBoardId(boardId);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Получить историю пользователя", description = "Возвращает действия пользователя")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActionHistoryResponseDTO>> getHistoryByUserId(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId) {
        
        List<ActionHistoryResponseDTO> history = actionHistoryService.getHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Получить историю объекта", description = "Возвращает историю изменений конкретного объекта")
    @GetMapping("/board/{boardId}/target/{targetId}")
    public ResponseEntity<List<ActionHistoryResponseDTO>> getHistoryByTarget(
            @Parameter(description = "ID доски", required = true)
            @PathVariable Long boardId,
            @Parameter(description = "ID цели", required = true)
            @PathVariable Long targetId) {
        
        List<ActionHistoryResponseDTO> history = actionHistoryService.getHistoryByTarget(boardId, targetId);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Получить последнее действие", description = "Возвращает последнее действие для доски")
    @GetMapping("/board/{boardId}/latest")
    public ResponseEntity<ActionHistoryResponseDTO> getLatestAction(
            @Parameter(description = "ID доски", required = true)
            @PathVariable Long boardId) {
        
        ActionHistoryResponseDTO action = actionHistoryService.getLatestAction(boardId);
        if (action == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(action);
    }

    @Operation(summary = "Создать запись истории", description = "Создает новую запись о действии")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Запись создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping
    public ResponseEntity<ActionHistoryResponseDTO> createAction(
            @Parameter(description = "Данные действия", required = true)
            @Valid @RequestBody ActionHistoryRequestDTO dto) {
        
        ActionHistoryResponseDTO savedAction = actionHistoryService.createAction(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAction);
    }

    @Operation(summary = "Удалить запись истории", description = "Удаляет запись по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Удалено"),
        @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(
            @Parameter(description = "ID записи", required = true)
            @PathVariable Long id) {
        
        boolean deleted = actionHistoryService.deleteHistory(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}