package com.example.artship.board.controller;

import com.example.artship.board.dto.request.BrushPresetRequestDTO;
import com.example.artship.board.dto.response.BrushPresetResponseDTO;
import com.example.artship.board.service.BrushPresetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/brush-presets")
public class BrushPresetController {

    @Autowired
    private BrushPresetService brushPresetService;

    @GetMapping
    public ResponseEntity<List<BrushPresetResponseDTO>> getAllPresets() {
        List<BrushPresetResponseDTO> presets = brushPresetService.getAllPresets();
        return ResponseEntity.ok(presets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrushPresetResponseDTO> getPresetById(@PathVariable Long id) {
        Optional<BrushPresetResponseDTO> preset = brushPresetService.getPresetById(id);
        return preset.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BrushPresetResponseDTO>> getPresetsByUserId(@PathVariable Long userId) {
        List<BrushPresetResponseDTO> presets = brushPresetService.getPresetsByUserId(userId);
        return ResponseEntity.ok(presets);
    }

    @GetMapping("/public")
    public ResponseEntity<List<BrushPresetResponseDTO>> getPublicPresets() {
        List<BrushPresetResponseDTO> presets = brushPresetService.getPublicPresets();
        return ResponseEntity.ok(presets);
    }

    @PostMapping
    public ResponseEntity<BrushPresetResponseDTO> createPreset(@Valid @RequestBody BrushPresetRequestDTO presetDTO) {
        BrushPresetResponseDTO savedPreset = brushPresetService.createPreset(presetDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPreset);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrushPresetResponseDTO> updatePreset(@PathVariable Long id, @Valid @RequestBody BrushPresetRequestDTO presetDTO) {
        Optional<BrushPresetResponseDTO> updatedPreset = brushPresetService.updatePreset(id, presetDTO);
        return updatedPreset.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreset(@PathVariable Long id) {
        boolean deleted = brushPresetService.deletePreset(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}