package com.example.artship.board.service;

import com.example.artship.board.dto.request.RoomRequestDTO;
import com.example.artship.board.dto.response.RoomResponseDTO;
import com.example.artship.board.model.Room;
import com.example.artship.board.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    private RoomResponseDTO convertToDTO(Room room) {
        RoomResponseDTO dto = new RoomResponseDTO();
        dto.setId(room.getId());
        dto.setCreatorId(room.getCreatorId());
        dto.setTitle(room.getTitle());
        dto.setMaxPeople(room.getMaxPeople());
        dto.setPassword(room.getPassword());
        dto.setCreatedAt(room.getCreatedAt());
        return dto;
    }

    private Room convertToEntity(RoomRequestDTO dto) {
        Room room = new Room();
        room.setCreatorId(dto.getCreatorId());
        room.setTitle(dto.getTitle());
        room.setMaxPeople(dto.getMaxPeople());
        room.setPassword(dto.getPassword());
        room.setCreatedAt(LocalDateTime.now());
        return room;
    }

    public List<RoomResponseDTO> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<RoomResponseDTO> getRoomById(Long id) {
        return roomRepository.findById(id)
                .map(this::convertToDTO);
    }

    public RoomResponseDTO createRoom(RoomRequestDTO roomDTO) {
        Room room = convertToEntity(roomDTO);
        Room savedRoom = roomRepository.save(room);
        return convertToDTO(savedRoom);
    }

    public Optional<RoomResponseDTO> updateRoom(Long id, RoomRequestDTO roomDTO) {
        Optional<Room> optionalRoom = roomRepository.findById(id);
        
        if (optionalRoom.isEmpty()) {
            return Optional.empty();
        }

        Room room = optionalRoom.get();
        room.setCreatorId(roomDTO.getCreatorId());
        room.setTitle(roomDTO.getTitle());
        room.setMaxPeople(roomDTO.getMaxPeople());
        room.setPassword(roomDTO.getPassword());
        room.setCreatedAt(LocalDateTime.now());

        Room updatedRoom = roomRepository.save(room);
        return Optional.of(convertToDTO(updatedRoom));
    }

    public boolean deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            return false;
        }
        roomRepository.deleteById(id);
        return true;
    }
    
    public List<RoomResponseDTO> getRoomsByCreatorId(Long creatorId) {
        return roomRepository.findAll()
                .stream()
                .filter(room -> room.getCreatorId().equals(creatorId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<RoomResponseDTO> searchRoomsByTitle(String title) {
        return roomRepository.findAll()
                .stream()
                .filter(room -> room.getTitle().toLowerCase().contains(title.toLowerCase()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}