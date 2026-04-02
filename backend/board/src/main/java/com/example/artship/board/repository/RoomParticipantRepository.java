package com.example.artship.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.artship.board.model.RoomParticipant;

@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {
    List<RoomParticipant> findByRoomIdAndIsOnline(Long roomId, Boolean isOnline);
    List<RoomParticipant> findByUserId(Long userId);
    boolean existsByRoomIdAndUserId(Long roomId, Long userId);
}