package com.example.artship.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.artship.board.dto.request.ActiveCursorRequestDTO;
import com.example.artship.board.dto.response.ActiveCursorResponseDTO;
import com.example.artship.board.model.ActiveCursor;
import com.example.artship.board.repository.ActiveCursorRepository;

@Service
@Transactional
public class ActiveCursorService {

    @Autowired
    private ActiveCursorRepository activeCursorRepository;

    private ActiveCursorResponseDTO convertToDTO(ActiveCursor cursor) {
        ActiveCursorResponseDTO dto = new ActiveCursorResponseDTO();
        dto.setId(cursor.getId());
        dto.setRoomId(cursor.getRoomId());
        dto.setUserId(cursor.getUserId());
        dto.setXCoord(cursor.getXCoord());
        dto.setYCoord(cursor.getYCoord());
        dto.setColor(cursor.getColor());
        dto.setDisplayName(cursor.getDisplayName());
        dto.setLastUpdate(cursor.getLastUpdate());
        return dto;
    }

    private ActiveCursor convertToEntity(ActiveCursorRequestDTO dto) {
        ActiveCursor cursor = new ActiveCursor();
        cursor.setRoomId(dto.getRoomId());
        cursor.setUserId(dto.getUserId());
        cursor.setXCoord(dto.getXCoord());
        cursor.setYCoord(dto.getYCoord());
        cursor.setColor(dto.getColor());
        cursor.setDisplayName(dto.getDisplayName());
        cursor.setLastUpdate(LocalDateTime.now());
        return cursor;
    }

    public List<ActiveCursorResponseDTO> getAllCursors() {
        return activeCursorRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ActiveCursorResponseDTO> getCursorById(Long id) {
        return activeCursorRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<ActiveCursorResponseDTO> getCursorsByRoomId(Long roomId) {
        return activeCursorRepository.findByRoomId(roomId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ActiveCursorResponseDTO> getCursorsByUserId(Long userId) {
        return activeCursorRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ActiveCursorResponseDTO createOrUpdateCursor(ActiveCursorRequestDTO dto) {
        List<ActiveCursor> existingCursors = activeCursorRepository.findByRoomIdAndUserId(dto.getRoomId(), dto.getUserId());
        
        ActiveCursor cursor;
        if (existingCursors.isEmpty()) {
            cursor = convertToEntity(dto);
        } else {
            cursor = existingCursors.get(0);
            cursor.setXCoord(dto.getXCoord());
            cursor.setYCoord(dto.getYCoord());
            cursor.setColor(dto.getColor());
            cursor.setDisplayName(dto.getDisplayName());
            cursor.setLastUpdate(LocalDateTime.now());
        }
        
        ActiveCursor savedCursor = activeCursorRepository.save(cursor);
        return convertToDTO(savedCursor);
    }

    public Optional<ActiveCursorResponseDTO> updateCursor(Long id, ActiveCursorRequestDTO dto) {
        Optional<ActiveCursor> optionalCursor = activeCursorRepository.findById(id);
        
        if (optionalCursor.isEmpty()) {
            return Optional.empty();
        }

        ActiveCursor cursor = optionalCursor.get();
        if (dto.getXCoord() != null) {
            cursor.setXCoord(dto.getXCoord());
        }
        if (dto.getYCoord() != null) {
            cursor.setYCoord(dto.getYCoord());
        }
        if (dto.getColor() != null) {
            cursor.setColor(dto.getColor());
        }
        if (dto.getDisplayName() != null) {
            cursor.setDisplayName(dto.getDisplayName());
        }
        cursor.setLastUpdate(LocalDateTime.now());

        ActiveCursor updatedCursor = activeCursorRepository.save(cursor);
        return Optional.of(convertToDTO(updatedCursor));
    }

    public boolean deleteCursor(Long id) {
        if (!activeCursorRepository.existsById(id)) {
            return false;
        }
        activeCursorRepository.deleteById(id);
        return true;
    }

    public void deleteCursorsByRoomId(Long roomId) {
        activeCursorRepository.deleteByRoomId(roomId);
    }

    public void deleteCursorsByUserId(Long userId) {
        activeCursorRepository.deleteByUserId(userId);
    }
}