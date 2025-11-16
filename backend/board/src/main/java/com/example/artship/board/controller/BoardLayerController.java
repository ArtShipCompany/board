package com.example.artship.board.controller;

import com.example.artship.board.model.BoardLayer;
import com.example.artship.board.repository.BoardLayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/board-layers")
public class BoardLayerController {

    @Autowired
    private BoardLayerRepository boardLayerRepository;

    @GetMapping
    public List<BoardLayer> getAllLayers() {
        return boardLayerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardLayer> getLayerById(@PathVariable Long id) {
        Optional<BoardLayer> layer = boardLayerRepository.findById(id);
        return layer.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BoardLayer> createLayer(@RequestBody BoardLayer layer) {
        layer.setCreatedAt(LocalDateTime.now());

        BoardLayer savedLayer = boardLayerRepository.save(layer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLayer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardLayer> updateLayer(@PathVariable Long id, @RequestBody BoardLayer layerDetails) {
        Optional<BoardLayer> optionalLayer = boardLayerRepository.findById(id);

        if (optionalLayer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BoardLayer layer = optionalLayer.get();
        layer.setBoardId(layerDetails.getBoardId());
        layer.setName(layerDetails.getName());
        layer.setOrderIndex(layerDetails.getOrderIndex());
        layer.setOpacity(layerDetails.getOpacity());
        layer.setIsVisible(layerDetails.getIsVisible());
        layer.setCreatedAt(LocalDateTime.now()); // обновляем время

        BoardLayer updatedLayer = boardLayerRepository.save(layer);
        return ResponseEntity.ok(updatedLayer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLayer(@PathVariable Long id) {
        if (!boardLayerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        boardLayerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}