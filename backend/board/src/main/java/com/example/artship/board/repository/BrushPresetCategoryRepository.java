package com.example.artship.board.repository;

import com.example.artship.board.model.BrushPresetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrushPresetCategoryRepository extends JpaRepository<BrushPresetCategory, Long> {
}