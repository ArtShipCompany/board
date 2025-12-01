package com.example.artship.board.service;

import com.example.artship.board.dto.request.BrushPresetCategoryRequestDTO;
import com.example.artship.board.dto.response.BrushPresetCategoryResponseDTO;
import com.example.artship.board.model.BrushPresetCategory;
import com.example.artship.board.repository.BrushPresetCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BrushPresetCategoryService {

    @Autowired
    private BrushPresetCategoryRepository brushPresetCategoryRepository;

    private BrushPresetCategoryResponseDTO convertToDTO(BrushPresetCategory link) {
        BrushPresetCategoryResponseDTO dto = new BrushPresetCategoryResponseDTO();
        dto.setId(link.getId());
        dto.setBrushPresetId(link.getBrushPresetId());
        dto.setCategoryId(link.getCategoryId());
        dto.setCreatedAt(link.getCreatedAt());
        return dto;
    }

    private BrushPresetCategory convertToEntity(BrushPresetCategoryRequestDTO dto) {
        BrushPresetCategory link = new BrushPresetCategory();
        link.setBrushPresetId(dto.getBrushPresetId());
        link.setCategoryId(dto.getCategoryId());
        link.setCreatedAt(LocalDateTime.now());
        return link;
    }

    public List<BrushPresetCategoryResponseDTO> getAllLinks() {
        return brushPresetCategoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BrushPresetCategoryResponseDTO> getLinkById(Long id) {
        return brushPresetCategoryRepository.findById(id)
                .map(this::convertToDTO);
    }

    public BrushPresetCategoryResponseDTO createLink(BrushPresetCategoryRequestDTO linkDTO) {
        BrushPresetCategory link = convertToEntity(linkDTO);
        BrushPresetCategory savedLink = brushPresetCategoryRepository.save(link);
        return convertToDTO(savedLink);
    }

    public Optional<BrushPresetCategoryResponseDTO> updateLink(Long id, BrushPresetCategoryRequestDTO linkDTO) {
        Optional<BrushPresetCategory> optionalLink = brushPresetCategoryRepository.findById(id);
        
        if (optionalLink.isEmpty()) {
            return Optional.empty();
        }

        BrushPresetCategory link = optionalLink.get();
        link.setBrushPresetId(linkDTO.getBrushPresetId());
        link.setCategoryId(linkDTO.getCategoryId());
        link.setCreatedAt(LocalDateTime.now());

        BrushPresetCategory updatedLink = brushPresetCategoryRepository.save(link);
        return Optional.of(convertToDTO(updatedLink));
    }

    public boolean deleteLink(Long id) {
        if (!brushPresetCategoryRepository.existsById(id)) {
            return false;
        }
        brushPresetCategoryRepository.deleteById(id);
        return true;
    }
}