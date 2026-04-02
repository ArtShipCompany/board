package com.example.artship.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.artship.board.dto.request.RoomParticipantRequestDTO;
import com.example.artship.board.dto.response.RoomParticipantResponseDTO;
import com.example.artship.board.model.RoomParticipant;
import com.example.artship.board.repository.RoomParticipantRepository;

@Service
@Transactional
public class RoomParticipantService {

    @Autowired
    private RoomParticipantRepository roomParticipantRepository;

    private RoomParticipantResponseDTO convertToDTO(RoomParticipant participant) {
        RoomParticipantResponseDTO dto = new RoomParticipantResponseDTO();
        dto.setId(participant.getId());
        dto.setRoomId(participant.getRoomId());
        dto.setUserId(participant.getUserId());
        dto.setNickname(participant.getNickname());
        dto.setCursorColor(participant.getCursorColor());
        dto.setIsOnline(participant.getIsOnline());
        dto.setJoinedAt(participant.getJoinedAt());
        dto.setLastActiveAt(participant.getLastActiveAt());
        return dto;
    }

    private RoomParticipant convertToEntity(RoomParticipantRequestDTO dto) {
        RoomParticipant participant = new RoomParticipant();
        participant.setRoomId(dto.getRoomId());
        participant.setUserId(dto.getUserId());
        participant.setNickname(dto.getNickname());
        participant.setCursorColor(dto.getCursorColor() != null ? dto.getCursorColor() : "#000000");
        participant.setIsOnline(dto.getIsOnline() != null ? dto.getIsOnline() : true);
        participant.setJoinedAt(LocalDateTime.now());
        participant.setLastActiveAt(LocalDateTime.now());
        return participant;
    }

    public List<RoomParticipantResponseDTO> getAllParticipants() {
        return roomParticipantRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<RoomParticipantResponseDTO> getParticipantById(Long id) {
        return roomParticipantRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<RoomParticipantResponseDTO> getParticipantsByRoomId(Long roomId) {
        return roomParticipantRepository.findByRoomIdAndIsOnline(roomId, true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RoomParticipantResponseDTO createParticipant(RoomParticipantRequestDTO dto) {
        RoomParticipant participant = convertToEntity(dto);
        RoomParticipant savedParticipant = roomParticipantRepository.save(participant);
        return convertToDTO(savedParticipant);
    }

    public Optional<RoomParticipantResponseDTO> updateParticipant(Long id, RoomParticipantRequestDTO dto) {
        Optional<RoomParticipant> optionalParticipant = roomParticipantRepository.findById(id);
        
        if (optionalParticipant.isEmpty()) {
            return Optional.empty();
        }

        RoomParticipant participant = optionalParticipant.get();
        if (dto.getNickname() != null) {
            participant.setNickname(dto.getNickname());
        }
        if (dto.getCursorColor() != null) {
            participant.setCursorColor(dto.getCursorColor());
        }
        if (dto.getIsOnline() != null) {
            participant.setIsOnline(dto.getIsOnline());
        }
        participant.setLastActiveAt(LocalDateTime.now());

        RoomParticipant updatedParticipant = roomParticipantRepository.save(participant);
        return Optional.of(convertToDTO(updatedParticipant));
    }

    public boolean deleteParticipant(Long id) {
        if (!roomParticipantRepository.existsById(id)) {
            return false;
        }
        roomParticipantRepository.deleteById(id);
        return true;
    }

    public boolean participantExists(Long roomId, Long userId) {
        return roomParticipantRepository.existsByRoomIdAndUserId(roomId, userId);
    }
}