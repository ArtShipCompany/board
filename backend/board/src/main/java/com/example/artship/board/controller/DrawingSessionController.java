package com.example.artship.board.controller;

import com.example.artship.board.dto.request.DrawingSessionRequestDTO;
import com.example.artship.board.dto.response.DrawingSessionResponseDTO;
import com.example.artship.board.service.DrawingSessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/drawing-sessions")
public class DrawingSessionController {

    @Autowired
    private DrawingSessionService drawingSessionService;

    @GetMapping
    public ResponseEntity<List<DrawingSessionResponseDTO>> getAllSessions() {
        List<DrawingSessionResponseDTO> sessions = drawingSessionService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrawingSessionResponseDTO> getSessionById(@PathVariable Long id) {
        Optional<DrawingSessionResponseDTO> session = drawingSessionService.getSessionById(id);
        return session.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<DrawingSessionResponseDTO>> getSessionsByBoardId(@PathVariable Long boardId) {
        List<DrawingSessionResponseDTO> sessions = drawingSessionService.getSessionsByBoardId(boardId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DrawingSessionResponseDTO>> getSessionsByUserId(@PathVariable Long userId) {
        List<DrawingSessionResponseDTO> sessions = drawingSessionService.getSessionsByUserId(userId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/active")
    public ResponseEntity<List<DrawingSessionResponseDTO>> getActiveSessions() {
        List<DrawingSessionResponseDTO> sessions = drawingSessionService.getActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    @PostMapping
    public ResponseEntity<DrawingSessionResponseDTO> createSession(@Valid @RequestBody DrawingSessionRequestDTO sessionDTO) {
        DrawingSessionResponseDTO savedSession = drawingSessionService.createSession(sessionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSession);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrawingSessionResponseDTO> updateSession(@PathVariable Long id, @Valid @RequestBody DrawingSessionRequestDTO sessionDTO) {
        Optional<DrawingSessionResponseDTO> updatedSession = drawingSessionService.updateSession(id, sessionDTO);
        return updatedSession.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        boolean deleted = drawingSessionService.deleteSession(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}