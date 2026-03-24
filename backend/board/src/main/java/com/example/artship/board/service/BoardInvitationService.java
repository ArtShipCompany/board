package com.example.artship.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.artship.board.dto.request.BoardInvitationRequestDTO;
import com.example.artship.board.dto.response.BoardInvitationResponseDTO;
import com.example.artship.board.model.BoardInvitation;
import com.example.artship.board.repository.BoardInvitationRepository;

@Service
@Transactional
public class BoardInvitationService {

    @Autowired
    private BoardInvitationRepository boardInvitationRepository;

    private BoardInvitationResponseDTO convertToDTO(BoardInvitation invitation) {
        BoardInvitationResponseDTO dto = new BoardInvitationResponseDTO();
        dto.setId(invitation.getId());
        dto.setBoardId(invitation.getBoardId());
        dto.setEmail(invitation.getEmail());
        dto.setInvitationToken(invitation.getInvitationToken());
        dto.setExpiresAt(invitation.getExpiresAt());
        dto.setCreatedAt(invitation.getCreatedAt());
        return dto;
    }

    private BoardInvitation convertToEntity(BoardInvitationRequestDTO dto) {
        BoardInvitation invitation = new BoardInvitation();
        invitation.setBoardId(dto.getBoardId());
        invitation.setEmail(dto.getEmail());
        invitation.setInvitationToken(UUID.randomUUID().toString());
        invitation.setCreatedAt(LocalDateTime.now());
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));
        return invitation;
    }

    public List<BoardInvitationResponseDTO> getAllInvitations() {
        return boardInvitationRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BoardInvitationResponseDTO> getInvitationById(Long id) {
        return boardInvitationRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<BoardInvitationResponseDTO> getInvitationByToken(String token) {
        return boardInvitationRepository.findByInvitationToken(token)
                .map(this::convertToDTO);
    }

    public List<BoardInvitationResponseDTO> getInvitationsByBoardId(Long boardId) {
        return boardInvitationRepository.findByBoardId(boardId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BoardInvitationResponseDTO createInvitation(BoardInvitationRequestDTO dto) {
        BoardInvitation invitation = convertToEntity(dto);
        BoardInvitation savedInvitation = boardInvitationRepository.save(invitation);
        return convertToDTO(savedInvitation);
    }

    public boolean deleteInvitation(Long id) {
        if (!boardInvitationRepository.existsById(id)) {
            return false;
        }
        boardInvitationRepository.deleteById(id);
        return true;
    }

    public void deleteInvitationsByBoardId(Long boardId) {
        boardInvitationRepository.deleteByBoardId(boardId);
    }

    public boolean isInvitationValid(String token) {
        Optional<BoardInvitation> optionalInvitation = boardInvitationRepository.findByInvitationToken(token);
        
        if (optionalInvitation.isEmpty()) {
            return false;
        }
        
        BoardInvitation invitation = optionalInvitation.get();
        return invitation.getExpiresAt() != null && 
               invitation.getExpiresAt().isAfter(LocalDateTime.now());
    }

    public BoardInvitationResponseDTO acceptInvitation(String token, Long userId) {
        Optional<BoardInvitation> optionalInvitation = boardInvitationRepository.findByInvitationToken(token);
        
        if (optionalInvitation.isEmpty()) {
            throw new RuntimeException("Приглашение не найдено");
        }
        
        BoardInvitation invitation = optionalInvitation.get();
        
        if (invitation.getExpiresAt() != null && invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Приглашение истекло");
        }
        
        BoardInvitationResponseDTO response = convertToDTO(invitation);
        boardInvitationRepository.delete(invitation);
        
        return response;
    }
}