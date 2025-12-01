package com.example.artship.board.controller;

import com.example.artship.board.dto.request.BoardRequestDTO;
import com.example.artship.board.dto.response.BoardResponseDTO;
import com.example.artship.board.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardResponseDTO>> getAllBoards() {
        List<BoardResponseDTO> boards = boardService.getAllBoards();
        return ResponseEntity.ok(boards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> getBoardById(@PathVariable Long id) {
        Optional<BoardResponseDTO> board = boardService.getBoardById(id);
        return board.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BoardResponseDTO> createBoard(@Valid @RequestBody BoardRequestDTO boardDTO) {
        BoardResponseDTO savedBoard = boardService.createBoard(boardDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBoard);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> updateBoard(@PathVariable Long id, @Valid @RequestBody BoardRequestDTO boardDTO) {
        Optional<BoardResponseDTO> updatedBoard = boardService.updateBoard(id, boardDTO);
        return updatedBoard.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boolean deleted = boardService.deleteBoard(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}