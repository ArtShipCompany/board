package com.example.artship.board.repository;

import com.example.artship.board.model.DrawingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrawingSessionRepository extends JpaRepository<DrawingSession, Long> {
}