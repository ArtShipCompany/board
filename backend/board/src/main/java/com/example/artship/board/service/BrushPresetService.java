package com.example.artship.board.service;

import com.example.artship.board.dto.request.BrushPresetRequestDTO;
import com.example.artship.board.dto.response.BrushPresetResponseDTO;
import com.example.artship.board.model.BrushPreset;
import com.example.artship.board.repository.BrushPresetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BrushPresetService {

    @Autowired
    private BrushPresetRepository brushPresetRepository;

    private BrushPresetResponseDTO convertToDTO(BrushPreset preset) {
        BrushPresetResponseDTO dto = new BrushPresetResponseDTO();
        dto.setId(preset.getId());
        dto.setUserId(preset.getUserId());
        dto.setName(preset.getName());
        dto.setBrushType(preset.getBrushType());
        dto.setSize(preset.getSize());
        dto.setOpacity(preset.getOpacity());
        dto.setFlow(preset.getFlow());
        dto.setHardness(preset.getHardness());
        dto.setSpacing(preset.getSpacing());
        dto.setColor(preset.getColor());
        dto.setTextureUrl(preset.getTextureUrl());
        dto.setIsPublic(preset.getIsPublic());
        dto.setCreatedAt(preset.getCreatedAt());
        dto.setUpdatedAt(preset.getUpdatedAt());
        return dto;
    }

    private BrushPreset convertToEntity(BrushPresetRequestDTO dto) {
        BrushPreset preset = new BrushPreset();
        preset.setUserId(dto.getUserId());
        preset.setName(dto.getName());
        preset.setBrushType(dto.getBrushType());
        preset.setSize(dto.getSize());
        preset.setOpacity(dto.getOpacity());
        preset.setFlow(dto.getFlow());
        preset.setHardness(dto.getHardness());
        preset.setSpacing(dto.getSpacing());
        preset.setColor(dto.getColor());
        preset.setTextureUrl(dto.getTextureUrl());
        preset.setIsPublic(dto.getIsPublic());
        
        LocalDateTime now = LocalDateTime.now();
        preset.setCreatedAt(now);
        preset.setUpdatedAt(now);
        
        return preset;
    }

    public List<BrushPresetResponseDTO> getAllPresets() {
        return brushPresetRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BrushPresetResponseDTO> getPresetById(Long id) {
        return brushPresetRepository.findById(id)
                .map(this::convertToDTO);
    }

    public BrushPresetResponseDTO createPreset(BrushPresetRequestDTO presetDTO) {
        BrushPreset preset = convertToEntity(presetDTO);
        BrushPreset savedPreset = brushPresetRepository.save(preset);
        return convertToDTO(savedPreset);
    }

    public Optional<BrushPresetResponseDTO> updatePreset(Long id, BrushPresetRequestDTO presetDTO) {
        Optional<BrushPreset> optionalPreset = brushPresetRepository.findById(id);
        
        if (optionalPreset.isEmpty()) {
            return Optional.empty();
        }

        BrushPreset preset = optionalPreset.get();
        preset.setUserId(presetDTO.getUserId());
        preset.setName(presetDTO.getName());
        preset.setBrushType(presetDTO.getBrushType());
        preset.setSize(presetDTO.getSize());
        preset.setOpacity(presetDTO.getOpacity());
        preset.setFlow(presetDTO.getFlow());
        preset.setHardness(presetDTO.getHardness());
        preset.setSpacing(presetDTO.getSpacing());
        preset.setColor(presetDTO.getColor());
        preset.setTextureUrl(presetDTO.getTextureUrl());
        preset.setIsPublic(presetDTO.getIsPublic());
        preset.setUpdatedAt(LocalDateTime.now());

        BrushPreset updatedPreset = brushPresetRepository.save(preset);
        return Optional.of(convertToDTO(updatedPreset));
    }

    public boolean deletePreset(Long id) {
        if (!brushPresetRepository.existsById(id)) {
            return false;
        }
        brushPresetRepository.deleteById(id);
        return true;
    }
    
    public List<BrushPresetResponseDTO> getPresetsByUserId(Long userId) {
        return brushPresetRepository.findAll()
                .stream()
                .filter(preset -> preset.getUserId().equals(userId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<BrushPresetResponseDTO> getPublicPresets() {
        return brushPresetRepository.findAll()
                .stream()
                .filter(preset -> Boolean.TRUE.equals(preset.getIsPublic()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}