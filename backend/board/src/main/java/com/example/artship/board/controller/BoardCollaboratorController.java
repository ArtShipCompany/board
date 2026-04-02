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

import com.example.artship.board.dto.request.BoardCollaboratorRequestDTO;
import com.example.artship.board.dto.response.BoardCollaboratorResponseDTO;
import com.example.artship.board.service.BoardCollaboratorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/board-collaborators")
@Tag(name = "Board Collaborator Controller", description = "API для управления сотрудниками досок")
public class BoardCollaboratorController {

    @Autowired
    private BoardCollaboratorService boardCollaboratorService;

    @Operation(summary = "Получить всех сотрудников", description = "Возвращает список всех сотрудников досок")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка сотрудников")
    @GetMapping
    public ResponseEntity<List<BoardCollaboratorResponseDTO>> getAllCollaborators() {
        List<BoardCollaboratorResponseDTO> collaborators = boardCollaboratorService.getAllCollaborators();
        return ResponseEntity.ok(collaborators);
    }

    @Operation(summary = "Получить сотрудника по ID", description = "Возвращает сотрудника по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Сотрудник найден"),
        @ApiResponse(responseCode = "404", description = "Сотрудник не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BoardCollaboratorResponseDTO> getCollaboratorById(
            @Parameter(description = "ID сотрудника", required = true)
            @PathVariable Long id) {
        
        Optional<BoardCollaboratorResponseDTO> collaborator = boardCollaboratorService.getCollaboratorById(id);
        return collaborator.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить сотрудников доски", description = "Возвращает всех сотрудников указанной доски")
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<BoardCollaboratorResponseDTO>> getCollaboratorsByBoardId(
            @Parameter(description = "ID доски", required = true)
            @PathVariable Long boardId) {
        
        List<BoardCollaboratorResponseDTO> collaborators = boardCollaboratorService.getCollaboratorsByBoardId(boardId);
        return ResponseEntity.ok(collaborators);
    }

    @Operation(summary = "Получить доски пользователя", description = "Возвращает все доски, где пользователь является сотрудником")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BoardCollaboratorResponseDTO>> getCollaboratorsByUserId(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId) {
        
        List<BoardCollaboratorResponseDTO> collaborators = boardCollaboratorService.getCollaboratorsByUserId(userId);
        return ResponseEntity.ok(collaborators);
    }

    @Operation(summary = "Добавить сотрудника на доску", description = "Добавляет пользователя в качестве сотрудника доски")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Сотрудник успешно добавлен"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
        @ApiResponse(responseCode = "409", description = "Пользователь уже является сотрудником")
    })
    @PostMapping
    public ResponseEntity<BoardCollaboratorResponseDTO> createCollaborator(
            @Parameter(description = "Данные для добавления сотрудника", required = true)
            @Valid @RequestBody BoardCollaboratorRequestDTO dto) {
        
        BoardCollaboratorResponseDTO savedCollaborator = boardCollaboratorService.createCollaborator(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCollaborator);
    }

    @Operation(summary = "Обновить права сотрудника", description = "Обновляет права доступа сотрудника")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Права успешно обновлены"),
        @ApiResponse(responseCode = "404", description = "Сотрудник не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BoardCollaboratorResponseDTO> updateCollaborator(
            @Parameter(description = "ID сотрудника", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные права доступа", required = true)
            @Valid @RequestBody BoardCollaboratorRequestDTO dto) {
        
        Optional<BoardCollaboratorResponseDTO> updatedCollaborator = boardCollaboratorService.updateCollaborator(id, dto);
        return updatedCollaborator.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить сотрудника", description = "Удаляет сотрудника по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Сотрудник успешно удален"),
        @ApiResponse(responseCode = "404", description = "Сотрудник не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollaborator(
            @Parameter(description = "ID сотрудника", required = true)
            @PathVariable Long id) {
        
        boolean deleted = boardCollaboratorService.deleteCollaborator(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Удалить сотрудника из доски", description = "Удаляет пользователя из сотрудников указанной доски")
    @DeleteMapping("/board/{boardId}/user/{userId}")
    public ResponseEntity<Void> removeCollaboratorFromBoard(
            @Parameter(description = "ID доски", required = true)
            @PathVariable Long boardId,
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId) {
        
        boolean removed = boardCollaboratorService.removeCollaboratorFromBoard(boardId, userId);
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}