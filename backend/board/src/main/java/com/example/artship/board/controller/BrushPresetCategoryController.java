package com.example.artship.board.controller;

import com.example.artship.board.dto.request.BrushPresetCategoryRequestDTO;
import com.example.artship.board.dto.response.BrushPresetCategoryResponseDTO;
import com.example.artship.board.service.BrushPresetCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/brush-preset-categories")
public class BrushPresetCategoryController {

    @Autowired
    private BrushPresetCategoryService brushPresetCategoryService;

    @GetMapping
    public ResponseEntity<List<BrushPresetCategoryResponseDTO>> getAllLinks() {
        List<BrushPresetCategoryResponseDTO> links = brushPresetCategoryService.getAllLinks();
        return ResponseEntity.ok(links);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrushPresetCategoryResponseDTO> getLinkById(@PathVariable Long id) {
        Optional<BrushPresetCategoryResponseDTO> link = brushPresetCategoryService.getLinkById(id);
        return link.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BrushPresetCategoryResponseDTO> createLink(@Valid @RequestBody BrushPresetCategoryRequestDTO linkDTO) {
        BrushPresetCategoryResponseDTO savedLink = brushPresetCategoryService.createLink(linkDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLink);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrushPresetCategoryResponseDTO> updateLink(@PathVariable Long id, @Valid @RequestBody BrushPresetCategoryRequestDTO linkDTO) {
        Optional<BrushPresetCategoryResponseDTO> updatedLink = brushPresetCategoryService.updateLink(id, linkDTO);
        return updatedLink.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLink(@PathVariable Long id) {
        boolean deleted = brushPresetCategoryService.deleteLink(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}