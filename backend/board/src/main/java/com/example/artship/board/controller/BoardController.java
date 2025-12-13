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

import com.example.artship.board.dto.request.BoardRequestDTO;
import com.example.artship.board.dto.response.BoardResponseDTO;
import com.example.artship.board.service.BoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/boards")
@Tag(name = "Board Controller", description = "API для управления досками")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Operation(summary = "Получить все доски", description = "Возвращает список всех досок")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка досок")
    @GetMapping
    public ResponseEntity<List<BoardResponseDTO>> getAllBoards() {
        List<BoardResponseDTO> boards = boardService.getAllBoards();
        return ResponseEntity.ok(boards);
    }

    @Operation(summary = "Получить доску по ID", description = "Возвращает доску по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Доска найдена"),
        @ApiResponse(responseCode = "404", description = "Доска не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> getBoardById(
            @Parameter(description = "ID доски", required = true)
            @PathVariable Long id) {
        
        Optional<BoardResponseDTO> board = boardService.getBoardById(id);
        return board.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать новую доску", description = "Создает новую доску с указанными параметрами")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Доска успешно создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PostMapping
    public ResponseEntity<BoardResponseDTO> createBoard(
            @Parameter(description = "Данные для создания доски", required = true)
            @Valid @RequestBody BoardRequestDTO boardDTO) {
        
        BoardResponseDTO savedBoard = boardService.createBoard(boardDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBoard);
    }

    @Operation(summary = "Обновить доску", description = "Обновляет существующую доску по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Доска успешно обновлена"),
        @ApiResponse(responseCode = "404", description = "Доска не найдена"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> updateBoard(
            @Parameter(description = "ID доски", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные доски", required = true)
            @Valid @RequestBody BoardRequestDTO boardDTO) {
        
        Optional<BoardResponseDTO> updatedBoard = boardService.updateBoard(id, boardDTO);
        return updatedBoard.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить доску", description = "Удаляет доску по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Доска успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Доска не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @Parameter(description = "ID доски", required = true)
            @PathVariable Long id) {
        
        boolean deleted = boardService.deleteBoard(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}