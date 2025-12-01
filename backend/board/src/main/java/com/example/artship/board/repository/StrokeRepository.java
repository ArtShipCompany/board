package com.example.artship.board.repository;

import com.example.artship.board.model.Stroke;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrokeRepository extends JpaRepository<Stroke, Long> {
}