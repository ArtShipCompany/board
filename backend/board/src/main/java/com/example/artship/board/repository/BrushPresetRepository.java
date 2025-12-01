package com.example.artship.board.repository;

import com.example.artship.board.model.BrushPreset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrushPresetRepository extends JpaRepository<BrushPreset, Long> {
}