package com.example.artship.board.controller;

import com.example.artship.board.dto.request.StrokeRequestDTO;
import com.example.artship.board.dto.response.StrokeResponseDTO;
import com.example.artship.board.service.StrokeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/strokes")
public class StrokeController {

    @Autowired
    private StrokeService strokeService;

    @GetMapping
    public ResponseEntity<List<StrokeResponseDTO>> getAllStrokes() {
        List<StrokeResponseDTO> strokes = strokeService.getAllStrokes();
        return ResponseEntity.ok(strokes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StrokeResponseDTO> getStrokeById(@PathVariable Long id) {
        Optional<StrokeResponseDTO> stroke = strokeService.getStrokeById(id);
        return stroke.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<StrokeResponseDTO>> getStrokesBySessionId(@PathVariable Long sessionId) {
        List<StrokeResponseDTO> strokes = strokeService.getStrokesBySessionId(sessionId);
        return ResponseEntity.ok(strokes);
    }

    @GetMapping("/layer/{layerId}")
    public ResponseEntity<List<StrokeResponseDTO>> getStrokesByLayerId(@PathVariable Long layerId) {
        List<StrokeResponseDTO> strokes = strokeService.getStrokesByLayerId(layerId);
        return ResponseEntity.ok(strokes);
    }

    @GetMapping("/brush-preset/{brushPresetId}")
    public ResponseEntity<List<StrokeResponseDTO>> getStrokesByBrushPresetId(@PathVariable Long brushPresetId) {
        List<StrokeResponseDTO> strokes = strokeService.getStrokesByBrushPresetId(brushPresetId);
        return ResponseEntity.ok(strokes);
    }

    @PostMapping
    public ResponseEntity<StrokeResponseDTO> createStroke(@Valid @RequestBody StrokeRequestDTO strokeDTO) {
        StrokeResponseDTO savedStroke = strokeService.createStroke(strokeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStroke);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StrokeResponseDTO> updateStroke(@PathVariable Long id, @Valid @RequestBody StrokeRequestDTO strokeDTO) {
        Optional<StrokeResponseDTO> updatedStroke = strokeService.updateStroke(id, strokeDTO);
        return updatedStroke.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStroke(@PathVariable Long id) {
        boolean deleted = strokeService.deleteStroke(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}