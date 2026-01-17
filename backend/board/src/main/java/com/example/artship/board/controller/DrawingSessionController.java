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

import com.example.artship.board.dto.request.DrawingSessionRequestDTO;
import com.example.artship.board.dto.response.DrawingSessionResponseDTO;
import com.example.artship.board.service.DrawingSessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/drawing-sessions")
@Tag(name = "Drawing Session Controller", description = "API для управления сессиями рисования")
public class DrawingSessionController {

    @Autowired
    private DrawingSessionService drawingSessionService;

    @Operation(summary = "Получить все сессии рисования", 
               description = "Возвращает список всех сессий рисования")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка сессий")
    @GetMapping
    public ResponseEntity<List<DrawingSessionResponseDTO>> getAllSessions() {
        List<DrawingSessionResponseDTO> sessions = drawingSessionService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    @Operation(summary = "Получить сессию по ID", 
               description = "Возвращает сессию рисования по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Сессия найдена"),
        @ApiResponse(responseCode = "404", description = "Сессия не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DrawingSessionResponseDTO> getSessionById(
            @Parameter(description = "ID сессии", example = "1", required = true)
            @PathVariable Long id) {
        Optional<DrawingSessionResponseDTO> session = drawingSessionService.getSessionById(id);
        return session.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить сессии по ID доски", 
               description = "Возвращает все сессии рисования для указанной доски")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Сессии найдены"),
        @ApiResponse(responseCode = "404", description = "Доска не найдена")
    })
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<DrawingSessionResponseDTO>> getSessionsByBoardId(
            @Parameter(description = "ID доски", example = "1", required = true)
            @PathVariable Long boardId) {
        List<DrawingSessionResponseDTO> sessions = drawingSessionService.getSessionsByBoardId(boardId);
        return ResponseEntity.ok(sessions);
    }

    @Operation(summary = "Получить сессии по ID пользователя", 
               description = "Возвращает все сессии рисования, созданные указанным пользователем")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Сессии найдены"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DrawingSessionResponseDTO>> getSessionsByUserId(
            @Parameter(description = "ID пользователя", example = "123", required = true)
            @PathVariable Long userId) {
        List<DrawingSessionResponseDTO> sessions = drawingSessionService.getSessionsByUserId(userId);
        return ResponseEntity.ok(sessions);
    }

    @Operation(summary = "Получить активные сессии", 
               description = "Возвращает список активных (не завершенных) сессий рисования")
    @ApiResponse(responseCode = "200", description = "Успешное получение активных сессий")
    @GetMapping("/active")
    public ResponseEntity<List<DrawingSessionResponseDTO>> getActiveSessions() {
        List<DrawingSessionResponseDTO> sessions = drawingSessionService.getActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    @Operation(summary = "Создать новую сессию", 
               description = "Создает новую сессию рисования для доски")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Сессия успешно создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
        @ApiResponse(responseCode = "404", description = "Доска или пользователь не найдены")
    })
    @PostMapping
    public ResponseEntity<DrawingSessionResponseDTO> createSession(
            @Parameter(description = "Данные для создания сессии", required = true)
            @Valid @RequestBody DrawingSessionRequestDTO sessionDTO) {
        DrawingSessionResponseDTO savedSession = drawingSessionService.createSession(sessionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSession);
    }

    @Operation(summary = "Обновить сессию", 
               description = "Обновляет существующую сессию рисования")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Сессия успешно обновлена"),
        @ApiResponse(responseCode = "404", description = "Сессия не найдена"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен (не владелец сессии)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DrawingSessionResponseDTO> updateSession(
            @Parameter(description = "ID сессии", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные сессии", required = true)
            @Valid @RequestBody DrawingSessionRequestDTO sessionDTO) {
        Optional<DrawingSessionResponseDTO> updatedSession = drawingSessionService.updateSession(id, sessionDTO);
        return updatedSession.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить сессию", 
               description = "Удаляет сессию рисования по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Сессия успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Сессия не найдена"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен (не владелец сессии)")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(
            @Parameter(description = "ID сессии", example = "1", required = true)
            @PathVariable Long id) {
        boolean deleted = drawingSessionService.deleteSession(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}