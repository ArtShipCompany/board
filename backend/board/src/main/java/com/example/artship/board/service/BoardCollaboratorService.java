package com.example.artship.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.artship.board.dto.request.BoardCollaboratorRequestDTO;
import com.example.artship.board.dto.response.BoardCollaboratorResponseDTO;
import com.example.artship.board.model.BoardCollaborator;
import com.example.artship.board.repository.BoardCollaboratorRepository;

@Service
@Transactional
public class BoardCollaboratorService {

    @Autowired
    private BoardCollaboratorRepository boardCollaboratorRepository;

    private BoardCollaboratorResponseDTO convertToDTO(BoardCollaborator collaborator) {
        BoardCollaboratorResponseDTO dto = new BoardCollaboratorResponseDTO();
        dto.setId(collaborator.getId());
        dto.setBoardId(collaborator.getBoardId());
        dto.setUserId(collaborator.getUserId());
        dto.setPermission(collaborator.getPermission());
        dto.setAddedAt(collaborator.getAddedAt());
        return dto;
    }

    private BoardCollaborator convertToEntity(BoardCollaboratorRequestDTO dto) {
        BoardCollaborator collaborator = new BoardCollaborator();
        collaborator.setBoardId(dto.getBoardId());
        collaborator.setUserId(dto.getUserId());
        collaborator.setPermission(dto.getPermission());
        collaborator.setAddedAt(LocalDateTime.now());
        return collaborator;
    }

    public List<BoardCollaboratorResponseDTO> getAllCollaborators() {
        return boardCollaboratorRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BoardCollaboratorResponseDTO> getCollaboratorById(Long id) {
        return boardCollaboratorRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<BoardCollaboratorResponseDTO> getCollaboratorsByBoardId(Long boardId) {
        return boardCollaboratorRepository.findByBoardId(boardId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BoardCollaboratorResponseDTO> getCollaboratorsByUserId(Long userId) {
        return boardCollaboratorRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BoardCollaboratorResponseDTO> getCollaboratorByBoardIdAndUserId(Long boardId, Long userId) {
        return boardCollaboratorRepository.findByBoardIdAndUserId(boardId, userId)
                .map(this::convertToDTO);
    }

    public BoardCollaboratorResponseDTO createCollaborator(BoardCollaboratorRequestDTO dto) {
        if (boardCollaboratorRepository.existsByBoardIdAndUserId(dto.getBoardId(), dto.getUserId())) {
            throw new RuntimeException("Пользователь уже является сотрудником этой доски");
        }
        
        BoardCollaborator collaborator = convertToEntity(dto);
        BoardCollaborator savedCollaborator = boardCollaboratorRepository.save(collaborator);
        return convertToDTO(savedCollaborator);
    }

    public Optional<BoardCollaboratorResponseDTO> updateCollaborator(Long id, BoardCollaboratorRequestDTO dto) {
        Optional<BoardCollaborator> optionalCollaborator = boardCollaboratorRepository.findById(id);
        
        if (optionalCollaborator.isEmpty()) {
            return Optional.empty();
        }

        BoardCollaborator collaborator = optionalCollaborator.get();
        if (dto.getPermission() != null) {
            collaborator.setPermission(dto.getPermission());
        }
        collaborator.setAddedAt(LocalDateTime.now());

        BoardCollaborator updatedCollaborator = boardCollaboratorRepository.save(collaborator);
        return Optional.of(convertToDTO(updatedCollaborator));
    }

    public boolean deleteCollaborator(Long id) {
        if (!boardCollaboratorRepository.existsById(id)) {
            return false;
        }
        boardCollaboratorRepository.deleteById(id);
        return true;
    }

    public boolean removeCollaboratorFromBoard(Long boardId, Long userId) {
        Optional<BoardCollaborator> optionalCollaborator = boardCollaboratorRepository.findByBoardIdAndUserId(boardId, userId);
        
        if (optionalCollaborator.isEmpty()) {
            return false;
        }
        
        boardCollaboratorRepository.delete(optionalCollaborator.get());
        return true;
    }

    public void deleteCollaboratorsByBoardId(Long boardId) {
        boardCollaboratorRepository.deleteByBoardId(boardId);
    }

    public void deleteCollaboratorsByUserId(Long userId) {
        boardCollaboratorRepository.deleteByUserId(userId);
    }

    public boolean hasPermission(Long boardId, Long userId, String requiredPermission) {
        Optional<BoardCollaborator> optionalCollaborator = boardCollaboratorRepository.findByBoardIdAndUserId(boardId, userId);
        
        if (optionalCollaborator.isEmpty()) {
            return false;
        }
        
        String permission = optionalCollaborator.get().getPermission();
        if ("admin".equals(requiredPermission)) {
            return "admin".equals(permission);
        } else if ("edit".equals(requiredPermission)) {
            return "edit".equals(permission) || "admin".equals(permission);
        } else {
            return true;
        }
    }
}