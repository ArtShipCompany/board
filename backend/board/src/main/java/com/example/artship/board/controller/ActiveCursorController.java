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

import com.example.artship.board.dto.request.ActiveCursorRequestDTO;
import com.example.artship.board.dto.response.ActiveCursorResponseDTO;
import com.example.artship.board.service.ActiveCursorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/active-cursors")
@Tag(name = "Active Cursor Controller", description = "API для управления активными курсорами")
public class ActiveCursorController {

    @Autowired
    private ActiveCursorService activeCursorService;

    @Operation(summary = "Получить все курсоры", description = "Возвращает список всех активных курсоров")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка курсоров")
    @GetMapping
    public ResponseEntity<List<ActiveCursorResponseDTO>> getAllCursors() {
        List<ActiveCursorResponseDTO> cursors = activeCursorService.getAllCursors();
        return ResponseEntity.ok(cursors);
    }

    @Operation(summary = "Получить курсор по ID", description = "Возвращает курсор по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Курсор найден"),
        @ApiResponse(responseCode = "404", description = "Курсор не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ActiveCursorResponseDTO> getCursorById(
            @Parameter(description = "ID курсора", required = true)
            @PathVariable Long id) {
        
        Optional<ActiveCursorResponseDTO> cursor = activeCursorService.getCursorById(id);
        return cursor.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить курсоры комнаты", description = "Возвращает все активные курсоры указанной комнаты")
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ActiveCursorResponseDTO>> getCursorsByRoomId(
            @Parameter(description = "ID комнаты", required = true)
            @PathVariable Long roomId) {
        
        List<ActiveCursorResponseDTO> cursors = activeCursorService.getCursorsByRoomId(roomId);
        return ResponseEntity.ok(cursors);
    }

    @Operation(summary = "Получить курсоры пользователя", description = "Возвращает все активные курсоры пользователя")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActiveCursorResponseDTO>> getCursorsByUserId(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId) {
        
        List<ActiveCursorResponseDTO> cursors = activeCursorService.getCursorsByUserId(userId);
        return ResponseEntity.ok(cursors);
    }

    @Operation(summary = "Создать или обновить курсор", description = "Создает новый курсор или обновляет существующий")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Курсор успешно создан/обновлен"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PostMapping
    public ResponseEntity<ActiveCursorResponseDTO> createOrUpdateCursor(
            @Parameter(description = "Данные курсора", required = true)
            @Valid @RequestBody ActiveCursorRequestDTO dto) {
        
        ActiveCursorResponseDTO savedCursor = activeCursorService.createOrUpdateCursor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCursor);
    }

    @Operation(summary = "Обновить курсор", description = "Обновляет существующий курсор по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Курсор успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Курсор не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ActiveCursorResponseDTO> updateCursor(
            @Parameter(description = "ID курсора", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные курсора", required = true)
            @Valid @RequestBody ActiveCursorRequestDTO dto) {
        
        Optional<ActiveCursorResponseDTO> updatedCursor = activeCursorService.updateCursor(id, dto);
        return updatedCursor.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить курсор", description = "Удаляет курсор по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Курсор успешно удален"),
        @ApiResponse(responseCode = "404", description = "Курсор не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCursor(
            @Parameter(description = "ID курсора", required = true)
            @PathVariable Long id) {
        
        boolean deleted = activeCursorService.deleteCursor(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Удалить курсоры комнаты", description = "Удаляет все курсоры указанной комнаты")
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<Void> deleteCursorsByRoomId(
            @Parameter(description = "ID комнаты", required = true)
            @PathVariable Long roomId) {
        
        activeCursorService.deleteCursorsByRoomId(roomId);
        return ResponseEntity.noContent().build();
    }
}