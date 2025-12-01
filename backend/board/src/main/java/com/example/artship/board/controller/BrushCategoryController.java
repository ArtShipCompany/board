package com.example.artship.board.controller;

import com.example.artship.board.dto.request.BrushCategoryRequestDTO;
import com.example.artship.board.dto.response.BrushCategoryResponseDTO;
import com.example.artship.board.service.BrushCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/brush-categories")
public class BrushCategoryController {

    @Autowired
    private BrushCategoryService brushCategoryService;

    @GetMapping
    public ResponseEntity<List<BrushCategoryResponseDTO>> getAllCategories() {
        List<BrushCategoryResponseDTO> categories = brushCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrushCategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        Optional<BrushCategoryResponseDTO> category = brushCategoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BrushCategoryResponseDTO> createCategory(@Valid @RequestBody BrushCategoryRequestDTO categoryDTO) {
        BrushCategoryResponseDTO savedCategory = brushCategoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrushCategoryResponseDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody BrushCategoryRequestDTO categoryDTO) {
        Optional<BrushCategoryResponseDTO> updatedCategory = brushCategoryService.updateCategory(id, categoryDTO);
        return updatedCategory.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        boolean deleted = brushCategoryService.deleteCategory(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}