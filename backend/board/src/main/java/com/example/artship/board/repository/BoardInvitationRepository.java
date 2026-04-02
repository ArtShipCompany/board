package com.example.artship.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.artship.board.model.BoardInvitation;

@Repository
public interface BoardInvitationRepository extends JpaRepository<BoardInvitation, Long> {
    List<BoardInvitation> findByBoardId(Long boardId);
    Optional<BoardInvitation> findByInvitationToken(String invitationToken);
    void deleteByBoardId(Long boardId);
    boolean existsByInvitationToken(String invitationToken);
}