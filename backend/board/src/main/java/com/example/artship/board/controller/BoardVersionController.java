package com.example.artship.board.controller;

import com.example.artship.board.model.BoardVersion;
import com.example.artship.board.repository.BoardVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/board-versions")
public class BoardVersionController {

    @Autowired
    private BoardVersionRepository boardVersionRepository;

    @GetMapping
    public List<BoardVersion> getAllVersions() {
        return boardVersionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardVersion> getVersionById(@PathVariable Long id) {
        Optional<BoardVersion> version = boardVersionRepository.findById(id);
        return version.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BoardVersion> createVersion(@RequestBody BoardVersion version) {
        version.setCreatedAt(LocalDateTime.now());

        BoardVersion savedVersion = boardVersionRepository.save(version);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVersion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardVersion> updateVersion(@PathVariable Long id, @RequestBody BoardVersion versionDetails) {
        Optional<BoardVersion> optionalVersion = boardVersionRepository.findById(id);

        if (optionalVersion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BoardVersion version = optionalVersion.get();
        version.setBoardId(versionDetails.getBoardId());
        version.setUserId(versionDetails.getUserId());
        version.setVersionNumber(versionDetails.getVersionNumber());
        version.setDescription(versionDetails.getDescription());
        version.setSnapshotData(versionDetails.getSnapshotData());
        version.setCreatedAt(LocalDateTime.now());

        BoardVersion updatedVersion = boardVersionRepository.save(version);
        return ResponseEntity.ok(updatedVersion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVersion(@PathVariable Long id) {
        if (!boardVersionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        boardVersionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}