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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.artship.board.dto.request.BoardInvitationRequestDTO;
import com.example.artship.board.dto.response.BoardInvitationResponseDTO;
import com.example.artship.board.service.BoardInvitationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/board-invitations")
@Tag(name = "Board Invitation Controller", description = "API для управления приглашениями на доску")
public class BoardInvitationController {

    @Autowired
    private BoardInvitationService boardInvitationService;

    @Operation(summary = "Получить все приглашения", description = "Возвращает список всех приглашений")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка приглашений")
    @GetMapping
    public ResponseEntity<List<BoardInvitationResponseDTO>> getAllInvitations() {
        List<BoardInvitationResponseDTO> invitations = boardInvitationService.getAllInvitations();
        return ResponseEntity.ok(invitations);
    }

    @Operation(summary = "Получить приглашение по ID", description = "Возвращает приглашение по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Приглашение найдено"),
        @ApiResponse(responseCode = "404", description = "Приглашение не найдено")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BoardInvitationResponseDTO> getInvitationById(
            @Parameter(description = "ID приглашения", required = true)
            @PathVariable Long id) {
        
        Optional<BoardInvitationResponseDTO> invitation = boardInvitationService.getInvitationById(id);
        return invitation.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить приглашение по токену", description = "Возвращает приглашение по токену")
    @GetMapping("/token/{token}")
    public ResponseEntity<BoardInvitationResponseDTO> getInvitationByToken(
            @Parameter(description = "Токен приглашения", required = true)
            @PathVariable String token) {
        
        Optional<BoardInvitationResponseDTO> invitation = boardInvitationService.getInvitationByToken(token);
        return invitation.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить приглашения доски", description = "Возвращает все приглашения указанной доски")
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<BoardInvitationResponseDTO>> getInvitationsByBoardId(
            @Parameter(description = "ID доски", required = true)
            @PathVariable Long boardId) {
        
        List<BoardInvitationResponseDTO> invitations = boardInvitationService.getInvitationsByBoardId(boardId);
        return ResponseEntity.ok(invitations);
    }

    @Operation(summary = "Создать приглашение", description = "Создает новое приглашение на доску")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Приглашение успешно создано"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PostMapping
    public ResponseEntity<BoardInvitationResponseDTO> createInvitation(
            @Parameter(description = "Данные для создания приглашения", required = true)
            @Valid @RequestBody BoardInvitationRequestDTO dto) {
        
        BoardInvitationResponseDTO savedInvitation = boardInvitationService.createInvitation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedInvitation);
    }

    @Operation(summary = "Принять приглашение", description = "Принимает приглашение по токену")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Приглашение успешно принято"),
        @ApiResponse(responseCode = "404", description = "Приглашение не найдено"),
        @ApiResponse(responseCode = "400", description = "Приглашение истекло")
    })
    @PostMapping("/accept")
    public ResponseEntity<BoardInvitationResponseDTO> acceptInvitation(
            @Parameter(description = "Токен приглашения", required = true)
            @RequestParam String token,
            @Parameter(description = "ID пользователя", required = true)
            @RequestParam Long userId) {
        
        BoardInvitationResponseDTO acceptedInvitation = boardInvitationService.acceptInvitation(token, userId);
        return ResponseEntity.ok(acceptedInvitation);
    }

    @Operation(summary = "Проверить валидность приглашения", description = "Проверяет, действует ли приглашение")
    @GetMapping("/validate/{token}")
    public ResponseEntity<Boolean> validateInvitation(
            @Parameter(description = "Токен приглашения", required = true)
            @PathVariable String token) {
        
        boolean isValid = boardInvitationService.isInvitationValid(token);
        return ResponseEntity.ok(isValid);
    }

    @Operation(summary = "Удалить приглашение", description = "Удаляет приглашение по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Приглашение успешно удалено"),
        @ApiResponse(responseCode = "404", description = "Приглашение не найдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvitation(
            @Parameter(description = "ID приглашения", required = true)
            @PathVariable Long id) {
        
        boolean deleted = boardInvitationService.deleteInvitation(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}