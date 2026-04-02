package com.example.artship.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.artship.board.dto.request.CreatorRequestDTO;
import com.example.artship.board.dto.response.CreatorResponseDTO;
import com.example.artship.board.model.Creator;
import com.example.artship.board.repository.CreatorRepository;

@Service
@Transactional
public class CreatorService {

    @Autowired
    private CreatorRepository creatorRepository;

    private CreatorResponseDTO convertToDTO(Creator creator) {
        CreatorResponseDTO dto = new CreatorResponseDTO();
        dto.setId(creator.getId());
        dto.setName(creator.getName());
        dto.setCreatedAt(creator.getCreatedAt());
        return dto;
    }

    private Creator convertToEntity(CreatorRequestDTO dto) {
        Creator creator = new Creator();
        creator.setName(dto.getName());
        creator.setCreatedAt(LocalDateTime.now());
        return creator;
    }

    public List<CreatorResponseDTO> getAllCreators() {
        return creatorRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CreatorResponseDTO> getCreatorById(Long id) {
        return creatorRepository.findById(id)
                .map(this::convertToDTO);
    }

    public CreatorResponseDTO createCreator(CreatorRequestDTO creatorDTO) {
        Creator creator = convertToEntity(creatorDTO);
        Creator savedCreator = creatorRepository.save(creator);
        return convertToDTO(savedCreator);
    }

    public Optional<CreatorResponseDTO> updateCreator(Long id, CreatorRequestDTO creatorDTO) {
        Optional<Creator> optionalCreator = creatorRepository.findById(id);
        
        if (optionalCreator.isEmpty()) {
            return Optional.empty();
        }

        Creator creator = optionalCreator.get();
        creator.setName(creatorDTO.getName());
        creator.setCreatedAt(LocalDateTime.now());

        Creator updatedCreator = creatorRepository.save(creator);
        return Optional.of(convertToDTO(updatedCreator));
    }

    public boolean deleteCreator(Long id) {
        if (!creatorRepository.existsById(id)) {
            return false;
        }
        creatorRepository.deleteById(id);
        return true;
    }
}