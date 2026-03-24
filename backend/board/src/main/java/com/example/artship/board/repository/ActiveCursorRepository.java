package com.example.artship.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.artship.board.model.ActiveCursor;

@Repository
public interface ActiveCursorRepository extends JpaRepository<ActiveCursor, Long> {
    List<ActiveCursor> findByRoomId(Long roomId);
    List<ActiveCursor> findByUserId(Long userId);
    List<ActiveCursor> findByRoomIdAndUserId(Long roomId, Long userId);
    void deleteByRoomId(Long roomId);
    void deleteByUserId(Long userId);
}