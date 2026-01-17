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

import com.example.artship.board.dto.request.RoomRequestDTO;
import com.example.artship.board.dto.response.RoomResponseDTO;
import com.example.artship.board.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Room Controller", description = "API для управления комнатами для совместного рисования")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Operation(summary = "Получить все комнаты", 
               description = "Возвращает список всех комнат для совместного рисования")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка комнат")
    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> getAllRooms() {
        List<RoomResponseDTO> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Получить комнату по ID", 
               description = "Возвращает комнату для совместного рисования по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Комната найдена"),
        @ApiResponse(responseCode = "404", description = "Комната не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getRoomById(
            @Parameter(description = "ID комнаты", example = "1", required = true)
            @PathVariable Long id) {
        Optional<RoomResponseDTO> room = roomService.getRoomById(id);
        return room.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить комнаты по ID создателя", 
               description = "Возвращает все комнаты, созданные указанным пользователем")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Комнаты найдены"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<RoomResponseDTO>> getRoomsByCreatorId(
            @Parameter(description = "ID создателя комнаты", example = "123", required = true)
            @PathVariable Long creatorId) {
        List<RoomResponseDTO> rooms = roomService.getRoomsByCreatorId(creatorId);
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Поиск комнат по названию", 
               description = "Возвращает комнаты, название которых содержит указанный текст")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный поиск комнат"),
        @ApiResponse(responseCode = "400", description = "Не указан параметр поиска")
    })
    @GetMapping("/search")
    public ResponseEntity<List<RoomResponseDTO>> searchRoomsByTitle(
            @Parameter(description = "Текст для поиска в названии комнат", example = "арт", required = true)
            @RequestParam String title) {
        List<RoomResponseDTO> rooms = roomService.searchRoomsByTitle(title);
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Создать новую комнату", 
               description = "Создает новую комнату для совместного рисования")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Комната успешно создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping
    public ResponseEntity<RoomResponseDTO> createRoom(
            @Parameter(description = "Данные для создания комнаты", required = true)
            @Valid @RequestBody RoomRequestDTO roomDTO) {
        RoomResponseDTO savedRoom = roomService.createRoom(roomDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    @Operation(summary = "Обновить комнату", 
               description = "Обновляет существующую комнату для совместного рисования")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Комната успешно обновлена"),
        @ApiResponse(responseCode = "404", description = "Комната не найдена"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен (не создатель комнаты)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> updateRoom(
            @Parameter(description = "ID комнаты", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные комнаты", required = true)
            @Valid @RequestBody RoomRequestDTO roomDTO) {
        Optional<RoomResponseDTO> updatedRoom = roomService.updateRoom(id, roomDTO);
        return updatedRoom.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить комнату", 
               description = "Удаляет комнату для совместного рисования по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Комната успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Комната не найдена"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен (не создатель комнаты)")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(
            @Parameter(description = "ID комнаты", example = "1", required = true)
            @PathVariable Long id) {
        boolean deleted = roomService.deleteRoom(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}