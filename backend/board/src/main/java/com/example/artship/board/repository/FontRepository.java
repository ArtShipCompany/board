package com.example.artship.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.artship.board.model.Font;

@Repository
public interface FontRepository extends JpaRepository<Font, Long> {
    List<Font> findByIsActiveOrderByFamily(Boolean isActive);
    List<Font> findByFamily(String family);
    List<Font> findByIsSystem(Boolean isSystem);
}