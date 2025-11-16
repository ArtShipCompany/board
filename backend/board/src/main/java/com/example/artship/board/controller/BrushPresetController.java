package com.example.artship.board.controller;

import com.example.artship.board.model.BrushPreset;
import com.example.artship.board.repository.BrushPresetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/brush-presets")
public class BrushPresetController {

    @Autowired
    private BrushPresetRepository brushPresetRepository;

    @GetMapping
    public List<BrushPreset> getAllPresets() {
        return brushPresetRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrushPreset> getPresetById(@PathVariable Long id) {
        Optional<BrushPreset> preset = brushPresetRepository.findById(id);
        return preset.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BrushPreset> createPreset(@RequestBody BrushPreset preset) {
        preset.setCreatedAt(LocalDateTime.now());
        preset.setUpdatedAt(LocalDateTime.now());

        BrushPreset savedPreset = brushPresetRepository.save(preset);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPreset);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrushPreset> updatePreset(@PathVariable Long id, @RequestBody BrushPreset presetDetails) {
        Optional<BrushPreset> optionalPreset = brushPresetRepository.findById(id);

        if (optionalPreset.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BrushPreset preset = optionalPreset.get();
        preset.setUserId(presetDetails.getUserId());
        preset.setName(presetDetails.getName());
        preset.setBrushType(presetDetails.getBrushType());
        preset.setSize(presetDetails.getSize());
        preset.setOpacity(presetDetails.getOpacity());
        preset.setFlow(presetDetails.getFlow());
        preset.setHardness(presetDetails.getHardness());
        preset.setSpacing(presetDetails.getSpacing());
        preset.setColor(presetDetails.getColor());
        preset.setTextureUrl(presetDetails.getTextureUrl());
        preset.setIsPublic(presetDetails.getIsPublic());
        preset.setUpdatedAt(LocalDateTime.now()); // обновляем время

        BrushPreset updatedPreset = brushPresetRepository.save(preset);
        return ResponseEntity.ok(updatedPreset);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreset(@PathVariable Long id) {
        if (!brushPresetRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        brushPresetRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}