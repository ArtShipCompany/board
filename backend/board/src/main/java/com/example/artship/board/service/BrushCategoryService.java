package com.example.artship.board.service;

import com.example.artship.board.dto.request.BrushCategoryRequestDTO;
import com.example.artship.board.dto.response.BrushCategoryResponseDTO;
import com.example.artship.board.model.BrushCategory;
import com.example.artship.board.repository.BrushCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BrushCategoryService {

    @Autowired
    private BrushCategoryRepository brushCategoryRepository;

    private BrushCategoryResponseDTO convertToDTO(BrushCategory category) {
        BrushCategoryResponseDTO dto = new BrushCategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setOrderIndex(category.getOrderIndex());
        dto.setCreatedAt(category.getCreatedAt());
        return dto;
    }

    private BrushCategory convertToEntity(BrushCategoryRequestDTO dto) {
        BrushCategory category = new BrushCategory();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setOrderIndex(dto.getOrderIndex());
        category.setCreatedAt(LocalDateTime.now());
        return category;
    }

    public List<BrushCategoryResponseDTO> getAllCategories() {
        return brushCategoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BrushCategoryResponseDTO> getCategoryById(Long id) {
        return brushCategoryRepository.findById(id)
                .map(this::convertToDTO);
    }

    public BrushCategoryResponseDTO createCategory(BrushCategoryRequestDTO categoryDTO) {
        BrushCategory category = convertToEntity(categoryDTO);
        BrushCategory savedCategory = brushCategoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    public Optional<BrushCategoryResponseDTO> updateCategory(Long id, BrushCategoryRequestDTO categoryDTO) {
        Optional<BrushCategory> optionalCategory = brushCategoryRepository.findById(id);
        
        if (optionalCategory.isEmpty()) {
            return Optional.empty();
        }

        BrushCategory category = optionalCategory.get();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setOrderIndex(categoryDTO.getOrderIndex());
        category.setCreatedAt(LocalDateTime.now());

        BrushCategory updatedCategory = brushCategoryRepository.save(category);
        return Optional.of(convertToDTO(updatedCategory));
    }

    public boolean deleteCategory(Long id) {
        if (!brushCategoryRepository.existsById(id)) {
            return false;
        }
        brushCategoryRepository.deleteById(id);
        return true;
    }
}