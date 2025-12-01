package com.example.artship.board.controller;

import com.example.artship.board.dto.request.BoardLayerRequestDTO;
import com.example.artship.board.dto.response.BoardLayerResponseDTO;
import com.example.artship.board.service.BoardLayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/board-layers")
public class BoardLayerController {

    @Autowired
    private BoardLayerService boardLayerService;

    @GetMapping
    public ResponseEntity<List<BoardLayerResponseDTO>> getAllLayers() {
        List<BoardLayerResponseDTO> layers = boardLayerService.getAllLayers();
        return ResponseEntity.ok(layers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardLayerResponseDTO> getLayerById(@PathVariable Long id) {
        Optional<BoardLayerResponseDTO> layer = boardLayerService.getLayerById(id);
        return layer.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<BoardLayerResponseDTO>> getLayersByBoardId(@PathVariable Long boardId) {
        List<BoardLayerResponseDTO> layers = boardLayerService.getLayersByBoardId(boardId);
        return ResponseEntity.ok(layers);
    }

    @PostMapping
    public ResponseEntity<BoardLayerResponseDTO> createLayer(@Valid @RequestBody BoardLayerRequestDTO layerDTO) {
        BoardLayerResponseDTO savedLayer = boardLayerService.createLayer(layerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLayer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardLayerResponseDTO> updateLayer(@PathVariable Long id, @Valid @RequestBody BoardLayerRequestDTO layerDTO) {
        Optional<BoardLayerResponseDTO> updatedLayer = boardLayerService.updateLayer(id, layerDTO);
        return updatedLayer.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLayer(@PathVariable Long id) {
        boolean deleted = boardLayerService.deleteLayer(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}