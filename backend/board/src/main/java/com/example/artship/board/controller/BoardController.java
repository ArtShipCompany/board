package com.example.artship.board.controller;

import com.example.artship.board.model.Board;
import com.example.artship.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    @GetMapping
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        board.setCreated_at(java.time.LocalDateTime.now());
        board.setUpdated_at(java.time.LocalDateTime.now());

        Board savedBoard = boardRepository.save(board);
        return ResponseEntity.ok(savedBoard);
    }
}