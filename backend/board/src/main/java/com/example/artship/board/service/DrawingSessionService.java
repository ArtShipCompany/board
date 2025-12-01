package com.example.artship.board.service;

import com.example.artship.board.dto.request.DrawingSessionRequestDTO;
import com.example.artship.board.dto.response.DrawingSessionResponseDTO;
import com.example.artship.board.model.DrawingSession;
import com.example.artship.board.repository.DrawingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DrawingSessionService {

    @Autowired
    private DrawingSessionRepository drawingSessionRepository;

    private DrawingSessionResponseDTO convertToDTO(DrawingSession session) {
        DrawingSessionResponseDTO dto = new DrawingSessionResponseDTO();
        dto.setId(session.getId());
        dto.setBoardId(session.getBoardId());
        dto.setUserId(session.getUserId());
        dto.setBrushPresetId(session.getBrushPresetId());
        dto.setLayerId(session.getLayerId());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setStrokesCount(session.getStrokesCount());
        dto.setCreatedAt(session.getCreatedAt());
        return dto;
    }

    private DrawingSession convertToEntity(DrawingSessionRequestDTO dto) {
        DrawingSession session = new DrawingSession();
        session.setBoardId(dto.getBoardId());
        session.setUserId(dto.getUserId());
        session.setBrushPresetId(dto.getBrushPresetId());
        session.setLayerId(dto.getLayerId());
        session.setStartTime(dto.getStartTime());
        session.setEndTime(dto.getEndTime());
        session.setStrokesCount(dto.getStrokesCount());
        session.setCreatedAt(LocalDateTime.now());
        return session;
    }

    public List<DrawingSessionResponseDTO> getAllSessions() {
        return drawingSessionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<DrawingSessionResponseDTO> getSessionById(Long id) {
        return drawingSessionRepository.findById(id)
                .map(this::convertToDTO);
    }

    public DrawingSessionResponseDTO createSession(DrawingSessionRequestDTO sessionDTO) {
        DrawingSession session = convertToEntity(sessionDTO);
        DrawingSession savedSession = drawingSessionRepository.save(session);
        return convertToDTO(savedSession);
    }

    public Optional<DrawingSessionResponseDTO> updateSession(Long id, DrawingSessionRequestDTO sessionDTO) {
        Optional<DrawingSession> optionalSession = drawingSessionRepository.findById(id);
        
        if (optionalSession.isEmpty()) {
            return Optional.empty();
        }

        DrawingSession session = optionalSession.get();
        session.setBoardId(sessionDTO.getBoardId());
        session.setUserId(sessionDTO.getUserId());
        session.setBrushPresetId(sessionDTO.getBrushPresetId());
        session.setLayerId(sessionDTO.getLayerId());
        session.setStartTime(sessionDTO.getStartTime());
        session.setEndTime(sessionDTO.getEndTime());
        session.setStrokesCount(sessionDTO.getStrokesCount());
        session.setCreatedAt(LocalDateTime.now());

        DrawingSession updatedSession = drawingSessionRepository.save(session);
        return Optional.of(convertToDTO(updatedSession));
    }

    public boolean deleteSession(Long id) {
        if (!drawingSessionRepository.existsById(id)) {
            return false;
        }
        drawingSessionRepository.deleteById(id);
        return true;
    }
    
    public List<DrawingSessionResponseDTO> getSessionsByBoardId(Long boardId) {
        return drawingSessionRepository.findAll()
                .stream()
                .filter(session -> session.getBoardId().equals(boardId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<DrawingSessionResponseDTO> getSessionsByUserId(Long userId) {
        return drawingSessionRepository.findAll()
                .stream()
                .filter(session -> session.getUserId().equals(userId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<DrawingSessionResponseDTO> getActiveSessions() {
        return drawingSessionRepository.findAll()
                .stream()
                .filter(session -> session.getEndTime() == null)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}