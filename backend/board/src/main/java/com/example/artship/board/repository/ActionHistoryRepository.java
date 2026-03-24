package com.example.artship.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.artship.board.model.ActionHistory;

@Repository
public interface ActionHistoryRepository extends JpaRepository<ActionHistory, Long> {
    List<ActionHistory> findByBoardIdOrderByTimestampDesc(Long boardId);
    List<ActionHistory> findByUserIdOrderByTimestampDesc(Long userId);
    List<ActionHistory> findByBoardIdAndTargetIdOrderByTimestampDesc(Long boardId, Long targetId);
    void deleteByBoardId(Long boardId);
    void deleteBySessionId(Long sessionId);
}