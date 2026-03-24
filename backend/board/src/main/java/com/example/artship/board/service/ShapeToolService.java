package com.example.artship.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.artship.board.dto.request.ShapeToolRequestDTO;
import com.example.artship.board.dto.response.ShapeToolResponseDTO;
import com.example.artship.board.model.ShapeTool;
import com.example.artship.board.repository.ShapeToolRepository;

@Service
@Transactional
public class ShapeToolService {

    @Autowired
    private ShapeToolRepository shapeToolRepository;

    private ShapeToolResponseDTO convertToDTO(ShapeTool shapeTool) {
        ShapeToolResponseDTO dto = new ShapeToolResponseDTO();
        dto.setId(shapeTool.getId());
        dto.setName(shapeTool.getName());
        dto.setDisplayName(shapeTool.getDisplayName());
        dto.setIconUrl(shapeTool.getIconUrl());
        dto.setMinSize(shapeTool.getMinSize());
        dto.setMaxSize(shapeTool.getMaxSize());
        dto.setCanResize(shapeTool.getCanResize());
        dto.setCanRotate(shapeTool.getCanRotate());
        dto.setCanChangeColor(shapeTool.getCanChangeColor());
        dto.setCanChangeOpacity(shapeTool.getCanChangeOpacity());
        dto.setDefaultColor(shapeTool.getDefaultColor());
        dto.setDefaultOpacity(shapeTool.getDefaultOpacity());
        dto.setParametersSchema(shapeTool.getParametersSchema());
        dto.setIsActive(shapeTool.getIsActive());
        dto.setOrderIndex(shapeTool.getOrderIndex());
        dto.setCreatedAt(shapeTool.getCreatedAt());
        return dto;
    }

    private ShapeTool convertToEntity(ShapeToolRequestDTO dto) {
        ShapeTool shapeTool = new ShapeTool();
        shapeTool.setName(dto.getName());
        shapeTool.setDisplayName(dto.getDisplayName());
        shapeTool.setIconUrl(dto.getIconUrl());
        shapeTool.setMinSize(dto.getMinSize() != null ? dto.getMinSize() : 1);
        shapeTool.setMaxSize(dto.getMaxSize() != null ? dto.getMaxSize() : 1000);
        shapeTool.setCanResize(dto.getCanResize() != null ? dto.getCanResize() : true);
        shapeTool.setCanRotate(dto.getCanRotate() != null ? dto.getCanRotate() : true);
        shapeTool.setCanChangeColor(dto.getCanChangeColor() != null ? dto.getCanChangeColor() : true);
        shapeTool.setCanChangeOpacity(dto.getCanChangeOpacity() != null ? dto.getCanChangeOpacity() : true);
        shapeTool.setDefaultColor(dto.getDefaultColor());
        shapeTool.setDefaultOpacity(dto.getDefaultOpacity() != null ? dto.getDefaultOpacity() : new java.math.BigDecimal("1.0"));
        shapeTool.setParametersSchema(dto.getParametersSchema());
        shapeTool.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        shapeTool.setOrderIndex(dto.getOrderIndex());
        shapeTool.setCreatedAt(LocalDateTime.now());
        return shapeTool;
    }

    public List<ShapeToolResponseDTO> getAllShapeTools() {
        return shapeToolRepository.findAllByOrderByOrderIndex()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ShapeToolResponseDTO> getActiveShapeTools() {
        return shapeToolRepository.findByIsActiveOrderByOrderIndex(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ShapeToolResponseDTO> getShapeToolById(Long id) {
        return shapeToolRepository.findById(id)
                .map(this::convertToDTO);
    }

    public ShapeToolResponseDTO createShapeTool(ShapeToolRequestDTO dto) {
        ShapeTool shapeTool = convertToEntity(dto);
        ShapeTool savedShapeTool = shapeToolRepository.save(shapeTool);
        return convertToDTO(savedShapeTool);
    }

    public Optional<ShapeToolResponseDTO> updateShapeTool(Long id, ShapeToolRequestDTO dto) {
        Optional<ShapeTool> optionalShapeTool = shapeToolRepository.findById(id);
        
        if (optionalShapeTool.isEmpty()) {
            return Optional.empty();
        }

        ShapeTool shapeTool = optionalShapeTool.get();
        if (dto.getName() != null) {
            shapeTool.setName(dto.getName());
        }
        if (dto.getDisplayName() != null) {
            shapeTool.setDisplayName(dto.getDisplayName());
        }
        if (dto.getIconUrl() != null) {
            shapeTool.setIconUrl(dto.getIconUrl());
        }
        if (dto.getMinSize() != null) {
            shapeTool.setMinSize(dto.getMinSize());
        }
        if (dto.getMaxSize() != null) {
            shapeTool.setMaxSize(dto.getMaxSize());
        }
        if (dto.getCanResize() != null) {
            shapeTool.setCanResize(dto.getCanResize());
        }
        if (dto.getCanRotate() != null) {
            shapeTool.setCanRotate(dto.getCanRotate());
        }
        if (dto.getCanChangeColor() != null) {
            shapeTool.setCanChangeColor(dto.getCanChangeColor());
        }
        if (dto.getCanChangeOpacity() != null) {
            shapeTool.setCanChangeOpacity(dto.getCanChangeOpacity());
        }
        if (dto.getDefaultColor() != null) {
            shapeTool.setDefaultColor(dto.getDefaultColor());
        }
        if (dto.getDefaultOpacity() != null) {
            shapeTool.setDefaultOpacity(dto.getDefaultOpacity());
        }
        if (dto.getParametersSchema() != null) {
            shapeTool.setParametersSchema(dto.getParametersSchema());
        }
        if (dto.getIsActive() != null) {
            shapeTool.setIsActive(dto.getIsActive());
        }
        if (dto.getOrderIndex() != null) {
            shapeTool.setOrderIndex(dto.getOrderIndex());
        }

        ShapeTool updatedShapeTool = shapeToolRepository.save(shapeTool);
        return Optional.of(convertToDTO(updatedShapeTool));
    }

    public boolean deleteShapeTool(Long id) {
        if (!shapeToolRepository.existsById(id)) {
            return false;
        }
        shapeToolRepository.deleteById(id);
        return true;
    }

    public boolean toggleShapeToolActive(Long id) {
        Optional<ShapeTool> optionalShapeTool = shapeToolRepository.findById(id);
        
        if (optionalShapeTool.isEmpty()) {
            return false;
        }

        ShapeTool shapeTool = optionalShapeTool.get();
        shapeTool.setIsActive(!shapeTool.getIsActive());
        shapeToolRepository.save(shapeTool);
        return true;
    }
}