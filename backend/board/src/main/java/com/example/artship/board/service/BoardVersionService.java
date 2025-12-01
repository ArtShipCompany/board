package com.example.artship.board.service;

import com.example.artship.board.dto.request.BoardVersionRequestDTO;
import com.example.artship.board.dto.response.BoardVersionResponseDTO;
import com.example.artship.board.model.BoardVersion;
import com.example.artship.board.repository.BoardVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BoardVersionService {

    @Autowired
    private BoardVersionRepository boardVersionRepository;

    private BoardVersionResponseDTO convertToDTO(BoardVersion version) {
        BoardVersionResponseDTO dto = new BoardVersionResponseDTO();
        dto.setId(version.getId());
        dto.setBoardId(version.getBoardId());
        dto.setUserId(version.getUserId());
        dto.setVersionNumber(version.getVersionNumber());
        dto.setDescription(version.getDescription());
        dto.setSnapshotData(version.getSnapshotData());
        dto.setCreatedAt(version.getCreatedAt());
        return dto;
    }

    private BoardVersion convertToEntity(BoardVersionRequestDTO dto) {
        BoardVersion version = new BoardVersion();
        version.setBoardId(dto.getBoardId());
        version.setUserId(dto.getUserId());
        version.setVersionNumber(dto.getVersionNumber());
        version.setDescription(dto.getDescription());
        version.setSnapshotData(dto.getSnapshotData());
        version.setCreatedAt(LocalDateTime.now());
        return version;
    }

    public List<BoardVersionResponseDTO> getAllVersions() {
        return boardVersionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BoardVersionResponseDTO> getVersionById(Long id) {
        return boardVersionRepository.findById(id)
                .map(this::convertToDTO);
    }

    public BoardVersionResponseDTO createVersion(BoardVersionRequestDTO versionDTO) {
        BoardVersion version = convertToEntity(versionDTO);
        BoardVersion savedVersion = boardVersionRepository.save(version);
        return convertToDTO(savedVersion);
    }

    public Optional<BoardVersionResponseDTO> updateVersion(Long id, BoardVersionRequestDTO versionDTO) {
        Optional<BoardVersion> optionalVersion = boardVersionRepository.findById(id);
        
        if (optionalVersion.isEmpty()) {
            return Optional.empty();
        }

        BoardVersion version = optionalVersion.get();
        version.setBoardId(versionDTO.getBoardId());
        version.setUserId(versionDTO.getUserId());
        version.setVersionNumber(versionDTO.getVersionNumber());
        version.setDescription(versionDTO.getDescription());
        version.setSnapshotData(versionDTO.getSnapshotData());
        version.setCreatedAt(LocalDateTime.now());

        BoardVersion updatedVersion = boardVersionRepository.save(version);
        return Optional.of(convertToDTO(updatedVersion));
    }

    public boolean deleteVersion(Long id) {
        if (!boardVersionRepository.existsById(id)) {
            return false;
        }
        boardVersionRepository.deleteById(id);
        return true;
    }
}