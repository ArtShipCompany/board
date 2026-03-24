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

import com.example.artship.board.dto.request.RoomParticipantRequestDTO;
import com.example.artship.board.dto.response.RoomParticipantResponseDTO;
import com.example.artship.board.service.RoomParticipantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/room-participants")
@Tag(name = "Room Participant Controller", description = "API для управления участниками комнат")
public class RoomParticipantController {

    @Autowired
    private RoomParticipantService roomParticipantService;

    @Operation(summary = "Получить всех участников", description = "Возвращает список всех участников комнат")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка участников")
    @GetMapping
    public ResponseEntity<List<RoomParticipantResponseDTO>> getAllParticipants() {
        List<RoomParticipantResponseDTO> participants = roomParticipantService.getAllParticipants();
        return ResponseEntity.ok(participants);
    }

    @Operation(summary = "Получить участника по ID", description = "Возвращает участника по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Участник найден"),
        @ApiResponse(responseCode = "404", description = "Участник не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoomParticipantResponseDTO> getParticipantById(
            @Parameter(description = "ID участника", required = true)
            @PathVariable Long id) {
        
        Optional<RoomParticipantResponseDTO> participant = roomParticipantService.getParticipantById(id);
        return participant.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить участников комнаты", description = "Возвращает онлайн участников указанной комнаты")
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<RoomParticipantResponseDTO>> getParticipantsByRoomId(
            @Parameter(description = "ID комнаты", required = true)
            @PathVariable Long roomId) {
        
        List<RoomParticipantResponseDTO> participants = roomParticipantService.getParticipantsByRoomId(roomId);
        return ResponseEntity.ok(participants);
    }

    @Operation(summary = "Добавить участника в комнату", description = "Добавляет нового участника в комнату")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Участник успешно добавлен"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PostMapping
    public ResponseEntity<RoomParticipantResponseDTO> createParticipant(
            @Parameter(description = "Данные для добавления участника", required = true)
            @Valid @RequestBody RoomParticipantRequestDTO dto) {
        
        RoomParticipantResponseDTO savedParticipant = roomParticipantService.createParticipant(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedParticipant);
    }

    @Operation(summary = "Обновить участника", description = "Обновляет информацию об участнике")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Участник успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Участник не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RoomParticipantResponseDTO> updateParticipant(
            @Parameter(description = "ID участника", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные участника", required = true)
            @Valid @RequestBody RoomParticipantRequestDTO dto) {
        
        Optional<RoomParticipantResponseDTO> updatedParticipant = roomParticipantService.updateParticipant(id, dto);
        return updatedParticipant.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить участника", description = "Удаляет участника из комнаты")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Участник успешно удален"),
        @ApiResponse(responseCode = "404", description = "Участник не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(
            @Parameter(description = "ID участника", required = true)
            @PathVariable Long id) {
        
        boolean deleted = roomParticipantService.deleteParticipant(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}