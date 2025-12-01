package com.example.artship.board.controller;

import com.example.artship.board.dto.request.BoardVersionRequestDTO;
import com.example.artship.board.dto.response.BoardVersionResponseDTO;
import com.example.artship.board.service.BoardVersionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/board-versions")
public class BoardVersionController {

    @Autowired
    private BoardVersionService boardVersionService;

    @GetMapping
    public ResponseEntity<List<BoardVersionResponseDTO>> getAllVersions() {
        List<BoardVersionResponseDTO> versions = boardVersionService.getAllVersions();
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardVersionResponseDTO> getVersionById(@PathVariable Long id) {
        Optional<BoardVersionResponseDTO> version = boardVersionService.getVersionById(id);
        return version.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BoardVersionResponseDTO> createVersion(
            @Valid @RequestBody BoardVersionRequestDTO versionDTO) {
        BoardVersionResponseDTO savedVersion = boardVersionService.createVersion(versionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVersion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardVersionResponseDTO> updateVersion(
            @PathVariable Long id,
            @Valid @RequestBody BoardVersionRequestDTO versionDTO) {
        Optional<BoardVersionResponseDTO> updatedVersion = boardVersionService.updateVersion(id, versionDTO);
        return updatedVersion.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVersion(@PathVariable Long id) {
        boolean deleted = boardVersionService.deleteVersion(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}