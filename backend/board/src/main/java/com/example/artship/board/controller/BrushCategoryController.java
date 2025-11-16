package com.example.artship.board.controller;

import com.example.artship.board.model.BrushCategory;
import com.example.artship.board.repository.BrushCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/brush-categories")
public class BrushCategoryController {

    @Autowired
    private BrushCategoryRepository brushCategoryRepository;

    @GetMapping
    public List<BrushCategory> getAllCategories() {
        return brushCategoryRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrushCategory> getCategoryById(@PathVariable Long id) {
        Optional<BrushCategory> category = brushCategoryRepository.findById(id);
        return category.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BrushCategory> createCategory(@RequestBody BrushCategory category) {
        category.setCreatedAt(LocalDateTime.now());

        BrushCategory savedCategory = brushCategoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrushCategory> updateCategory(@PathVariable Long id, @RequestBody BrushCategory categoryDetails) {
        Optional<BrushCategory> optionalCategory = brushCategoryRepository.findById(id);

        if (optionalCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BrushCategory category = optionalCategory.get();
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setOrderIndex(categoryDetails.getOrderIndex());
        category.setCreatedAt(LocalDateTime.now());

        BrushCategory updatedCategory = brushCategoryRepository.save(category);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (!brushCategoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        brushCategoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}