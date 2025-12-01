package com.example.artship.board.controller;

import com.example.artship.board.dto.request.RoomRequestDTO;
import com.example.artship.board.dto.response.RoomResponseDTO;
import com.example.artship.board.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> getAllRooms() {
        List<RoomResponseDTO> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getRoomById(@PathVariable Long id) {
        Optional<RoomResponseDTO> room = roomService.getRoomById(id);
        return room.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<RoomResponseDTO>> getRoomsByCreatorId(@PathVariable Long creatorId) {
        List<RoomResponseDTO> rooms = roomService.getRoomsByCreatorId(creatorId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RoomResponseDTO>> searchRoomsByTitle(@RequestParam String title) {
        List<RoomResponseDTO> rooms = roomService.searchRoomsByTitle(title);
        return ResponseEntity.ok(rooms);
    }

    @PostMapping
    public ResponseEntity<RoomResponseDTO> createRoom(@Valid @RequestBody RoomRequestDTO roomDTO) {
        RoomResponseDTO savedRoom = roomService.createRoom(roomDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequestDTO roomDTO) {
        Optional<RoomResponseDTO> updatedRoom = roomService.updateRoom(id, roomDTO);
        return updatedRoom.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        boolean deleted = roomService.deleteRoom(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}