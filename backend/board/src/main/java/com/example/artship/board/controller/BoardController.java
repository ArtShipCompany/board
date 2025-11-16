package com.example.artship.board.controller;

import com.example.artship.board.model.Board;
import com.example.artship.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    @GetMapping
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable Long id) {
        Optional<Board> board = boardRepository.findById(id);
        return board.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        LocalDateTime now = LocalDateTime.now();
        board.setCreated_at(now);
        board.setUpdated_at(now);

        Board savedBoard = boardRepository.save(board);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBoard);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long id, @RequestBody Board boardDetails) {
        Optional<Board> optionalBoard = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Board board = optionalBoard.get();
        board.setDescription(boardDetails.getDescription());
        board.setWidth(boardDetails.getWidth());
        board.setHeight(boardDetails.getHeight());
        board.setBackground_color(boardDetails.getBackground_color());
        board.setBackground_image(boardDetails.getBackground_image());
        board.setUpdated_at(LocalDateTime.now());

        Board updatedBoard = boardRepository.save(board);
        return ResponseEntity.ok(updatedBoard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        if (!boardRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        boardRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}