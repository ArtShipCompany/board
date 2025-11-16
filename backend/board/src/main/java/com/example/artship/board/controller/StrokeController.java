package com.example.artship.board.controller;

import com.example.artship.board.model.Stroke;
import com.example.artship.board.repository.StrokeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/strokes")
public class StrokeController {

    @Autowired
    private StrokeRepository strokeRepository;

    @GetMapping
    public List<Stroke> getAllStrokes() {
        return strokeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stroke> getStrokeById(@PathVariable Long id) {
        Optional<Stroke> stroke = strokeRepository.findById(id);
        return stroke.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Stroke> createStroke(@RequestBody Stroke stroke) {
        stroke.setCreatedAt(LocalDateTime.now());

        Stroke savedStroke = strokeRepository.save(stroke);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStroke);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stroke> updateStroke(@PathVariable Long id, @RequestBody Stroke strokeDetails) {
        Optional<Stroke> optionalStroke = strokeRepository.findById(id);

        if (optionalStroke.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Stroke stroke = optionalStroke.get();
        stroke.setSessionId(strokeDetails.getSessionId());
        stroke.setBrushPresetId(strokeDetails.getBrushPresetId());
        stroke.setLayerId(strokeDetails.getLayerId());
        stroke.setColor(strokeDetails.getColor());
        stroke.setSize(strokeDetails.getSize());
        stroke.setOpacity(strokeDetails.getOpacity());
        stroke.setPoints(strokeDetails.getPoints());
        stroke.setCreatedAt(LocalDateTime.now());

        Stroke updatedStroke = strokeRepository.save(stroke);
        return ResponseEntity.ok(updatedStroke);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStroke(@PathVariable Long id) {
        if (!strokeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        strokeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}