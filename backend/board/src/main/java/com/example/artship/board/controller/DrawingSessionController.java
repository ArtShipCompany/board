package com.example.artship.board.controller;

import com.example.artship.board.model.DrawingSession;
import com.example.artship.board.repository.DrawingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/drawing-sessions")
public class DrawingSessionController {

    @Autowired
    private DrawingSessionRepository drawingSessionRepository;

    @GetMapping
    public List<DrawingSession> getAllSessions() {
        return drawingSessionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrawingSession> getSessionById(@PathVariable Long id) {
        Optional<DrawingSession> session = drawingSessionRepository.findById(id);
        return session.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DrawingSession> createSession(@RequestBody DrawingSession session) {
        session.setCreatedAt(LocalDateTime.now());

        DrawingSession savedSession = drawingSessionRepository.save(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSession);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DrawingSession> updateSession(@PathVariable Long id, @RequestBody DrawingSession sessionDetails) {
        Optional<DrawingSession> optionalSession = drawingSessionRepository.findById(id);

        if (optionalSession.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DrawingSession session = optionalSession.get();
        session.setBoardId(sessionDetails.getBoardId());
        session.setUserId(sessionDetails.getUserId());
        session.setBrushPresetId(sessionDetails.getBrushPresetId());
        session.setLayerId(sessionDetails.getLayerId());
        session.setStartTime(sessionDetails.getStartTime());
        session.setEndTime(sessionDetails.getEndTime());
        session.setStrokesCount(sessionDetails.getStrokesCount());
        session.setCreatedAt(LocalDateTime.now());

        DrawingSession updatedSession = drawingSessionRepository.save(session);
        return ResponseEntity.ok(updatedSession);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        if (!drawingSessionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        drawingSessionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}