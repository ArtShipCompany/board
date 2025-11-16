package com.example.artship.board.repository;

import com.example.artship.board.model.BoardVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardVersionRepository extends JpaRepository<BoardVersion, Long> {
}