package com.example.artship.board.controller;

import com.example.artship.board.model.BrushPresetCategory;
import com.example.artship.board.repository.BrushPresetCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/brush-preset-categories")
public class BrushPresetCategoryController {

    @Autowired
    private BrushPresetCategoryRepository brushPresetCategoryRepository;

    @GetMapping
    public List<BrushPresetCategory> getAllLinks() {
        return brushPresetCategoryRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrushPresetCategory> getLinkById(@PathVariable Long id) {
        Optional<BrushPresetCategory> link = brushPresetCategoryRepository.findById(id);
        return link.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BrushPresetCategory> createLink(@RequestBody BrushPresetCategory link) {
        link.setCreatedAt(LocalDateTime.now());

        BrushPresetCategory savedLink = brushPresetCategoryRepository.save(link);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLink);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrushPresetCategory> updateLink(@PathVariable Long id, @RequestBody BrushPresetCategory linkDetails) {
        Optional<BrushPresetCategory> optionalLink = brushPresetCategoryRepository.findById(id);

        if (optionalLink.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BrushPresetCategory link = optionalLink.get();
        link.setBrushPresetId(linkDetails.getBrushPresetId());
        link.setCategoryId(linkDetails.getCategoryId());
        link.setCreatedAt(LocalDateTime.now());

        BrushPresetCategory updatedLink = brushPresetCategoryRepository.save(link);
        return ResponseEntity.ok(updatedLink);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLink(@PathVariable Long id) {
        if (!brushPresetCategoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        brushPresetCategoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}