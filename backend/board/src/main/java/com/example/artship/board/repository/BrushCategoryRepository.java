package com.example.artship.board.repository;

import com.example.artship.board.model.BrushCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrushCategoryRepository extends JpaRepository<BrushCategory, Long> {
}