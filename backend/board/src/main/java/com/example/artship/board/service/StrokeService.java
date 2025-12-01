package com.example.artship.board.service;

import com.example.artship.board.dto.request.StrokeRequestDTO;
import com.example.artship.board.dto.response.StrokeResponseDTO;
import com.example.artship.board.model.Stroke;
import com.example.artship.board.repository.StrokeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StrokeService {

    @Autowired
    private StrokeRepository strokeRepository;

    private StrokeResponseDTO convertToDTO(Stroke stroke) {
        StrokeResponseDTO dto = new StrokeResponseDTO();
        dto.setId(stroke.getId());
        dto.setSessionId(stroke.getSessionId());
        dto.setBrushPresetId(stroke.getBrushPresetId());
        dto.setLayerId(stroke.getLayerId());
        dto.setColor(stroke.getColor());
        dto.setSize(stroke.getSize());
        dto.setOpacity(stroke.getOpacity());
        dto.setPoints(stroke.getPoints());
        dto.setCreatedAt(stroke.getCreatedAt());
        return dto;
    }

    private Stroke convertToEntity(StrokeRequestDTO dto) {
        Stroke stroke = new Stroke();
        stroke.setSessionId(dto.getSessionId());
        stroke.setBrushPresetId(dto.getBrushPresetId());
        stroke.setLayerId(dto.getLayerId());
        stroke.setColor(dto.getColor());
        stroke.setSize(dto.getSize());
        stroke.setOpacity(dto.getOpacity());
        stroke.setPoints(dto.getPoints());
        stroke.setCreatedAt(LocalDateTime.now());
        return stroke;
    }

    public List<StrokeResponseDTO> getAllStrokes() {
        return strokeRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<StrokeResponseDTO> getStrokeById(Long id) {
        return strokeRepository.findById(id)
                .map(this::convertToDTO);
    }

    public StrokeResponseDTO createStroke(StrokeRequestDTO strokeDTO) {
        Stroke stroke = convertToEntity(strokeDTO);
        Stroke savedStroke = strokeRepository.save(stroke);
        return convertToDTO(savedStroke);
    }

    public Optional<StrokeResponseDTO> updateStroke(Long id, StrokeRequestDTO strokeDTO) {
        Optional<Stroke> optionalStroke = strokeRepository.findById(id);
        
        if (optionalStroke.isEmpty()) {
            return Optional.empty();
        }

        Stroke stroke = optionalStroke.get();
        stroke.setSessionId(strokeDTO.getSessionId());
        stroke.setBrushPresetId(strokeDTO.getBrushPresetId());
        stroke.setLayerId(strokeDTO.getLayerId());
        stroke.setColor(strokeDTO.getColor());
        stroke.setSize(strokeDTO.getSize());
        stroke.setOpacity(strokeDTO.getOpacity());
        stroke.setPoints(strokeDTO.getPoints());
        stroke.setCreatedAt(LocalDateTime.now());

        Stroke updatedStroke = strokeRepository.save(stroke);
        return Optional.of(convertToDTO(updatedStroke));
    }

    public boolean deleteStroke(Long id) {
        if (!strokeRepository.existsById(id)) {
            return false;
        }
        strokeRepository.deleteById(id);
        return true;
    }
    
    public List<StrokeResponseDTO> getStrokesBySessionId(Long sessionId) {
        return strokeRepository.findAll()
                .stream()
                .filter(stroke -> stroke.getSessionId().equals(sessionId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<StrokeResponseDTO> getStrokesByLayerId(Long layerId) {
        return strokeRepository.findAll()
                .stream()
                .filter(stroke -> stroke.getLayerId().equals(layerId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<StrokeResponseDTO> getStrokesByBrushPresetId(Long brushPresetId) {
        return strokeRepository.findAll()
                .stream()
                .filter(stroke -> stroke.getBrushPresetId().equals(brushPresetId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}