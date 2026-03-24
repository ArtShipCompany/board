package com.example.artship.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.artship.board.model.BoardCollaborator;

@Repository
public interface BoardCollaboratorRepository extends JpaRepository<BoardCollaborator, Long> {
    List<BoardCollaborator> findByBoardId(Long boardId);
    List<BoardCollaborator> findByUserId(Long userId);
    Optional<BoardCollaborator> findByBoardIdAndUserId(Long boardId, Long userId);
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);
    void deleteByBoardId(Long boardId);
    void deleteByUserId(Long userId);
}