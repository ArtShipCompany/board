package com.example.artship.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.artship.board.dto.request.FontRequestDTO;
import com.example.artship.board.dto.response.FontResponseDTO;
import com.example.artship.board.model.Font;
import com.example.artship.board.repository.FontRepository;

@Service
@Transactional
public class FontService {

    @Autowired
    private FontRepository fontRepository;

    private FontResponseDTO convertToDTO(Font font) {
        FontResponseDTO dto = new FontResponseDTO();
        dto.setId(font.getId());
        dto.setName(font.getName());
        dto.setFamily(font.getFamily());
        dto.setFileUrl(font.getFileUrl());
        dto.setFileFormat(font.getFileFormat());
        dto.setWeight(font.getWeight());
        dto.setStyle(font.getStyle());
        dto.setIsSystem(font.getIsSystem());
        dto.setIsActive(font.getIsActive());
        dto.setUploadedBy(font.getUploadedBy());
        dto.setCreatedAt(font.getCreatedAt());
        return dto;
    }

    private Font convertToEntity(FontRequestDTO dto) {
        Font font = new Font();
        font.setName(dto.getName());
        font.setFamily(dto.getFamily());
        font.setFileUrl(dto.getFileUrl());
        font.setFileFormat(dto.getFileFormat() != null ? dto.getFileFormat() : "woff2");
        font.setWeight(dto.getWeight() != null ? dto.getWeight() : "normal");
        font.setStyle(dto.getStyle() != null ? dto.getStyle() : "normal");
        font.setIsSystem(dto.getIsSystem() != null ? dto.getIsSystem() : false);
        font.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        font.setUploadedBy(dto.getUploadedBy());
        font.setCreatedAt(LocalDateTime.now());
        return font;
    }

    public List<FontResponseDTO> getAllFonts() {
        return fontRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FontResponseDTO> getActiveFonts() {
        return fontRepository.findByIsActiveOrderByFamily(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FontResponseDTO> getSystemFonts() {
        return fontRepository.findByIsSystem(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FontResponseDTO> getFontsByFamily(String family) {
        return fontRepository.findByFamily(family)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<FontResponseDTO> getFontById(Long id) {
        return fontRepository.findById(id)
                .map(this::convertToDTO);
    }

    public FontResponseDTO createFont(FontRequestDTO dto) {
        Font font = convertToEntity(dto);
        Font savedFont = fontRepository.save(font);
        return convertToDTO(savedFont);
    }

    public Optional<FontResponseDTO> updateFont(Long id, FontRequestDTO dto) {
        Optional<Font> optionalFont = fontRepository.findById(id);
        
        if (optionalFont.isEmpty()) {
            return Optional.empty();
        }

        Font font = optionalFont.get();
        if (dto.getName() != null) {
            font.setName(dto.getName());
        }
        if (dto.getFamily() != null) {
            font.setFamily(dto.getFamily());
        }
        if (dto.getFileUrl() != null) {
            font.setFileUrl(dto.getFileUrl());
        }
        if (dto.getFileFormat() != null) {
            font.setFileFormat(dto.getFileFormat());
        }
        if (dto.getWeight() != null) {
            font.setWeight(dto.getWeight());
        }
        if (dto.getStyle() != null) {
            font.setStyle(dto.getStyle());
        }
        if (dto.getIsSystem() != null) {
            font.setIsSystem(dto.getIsSystem());
        }
        if (dto.getIsActive() != null) {
            font.setIsActive(dto.getIsActive());
        }
        if (dto.getUploadedBy() != null) {
            font.setUploadedBy(dto.getUploadedBy());
        }

        Font updatedFont = fontRepository.save(font);
        return Optional.of(convertToDTO(updatedFont));
    }

    public boolean deleteFont(Long id) {
        if (!fontRepository.existsById(id)) {
            return false;
        }
        fontRepository.deleteById(id);
        return true;
    }

    public boolean toggleFontActive(Long id) {
        Optional<Font> optionalFont = fontRepository.findById(id);
        
        if (optionalFont.isEmpty()) {
            return false;
        }

        Font font = optionalFont.get();
        font.setIsActive(!font.getIsActive());
        fontRepository.save(font);
        return true;
    }
}