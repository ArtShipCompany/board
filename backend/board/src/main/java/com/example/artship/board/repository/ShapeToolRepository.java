package com.example.artship.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.artship.board.model.ShapeTool;

@Repository
public interface ShapeToolRepository extends JpaRepository<ShapeTool, Long> {
    List<ShapeTool> findByIsActiveOrderByOrderIndex(Boolean isActive);
    List<ShapeTool> findAllByOrderByOrderIndex();
}