package com.example.artship.board.service;

import com.example.artship.board.dto.request.BoardLayerRequestDTO;
import com.example.artship.board.dto.response.BoardLayerResponseDTO;
import com.example.artship.board.model.BoardLayer;
import com.example.artship.board.repository.BoardLayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BoardLayerService {

    @Autowired
    private BoardLayerRepository boardLayerRepository;

    private BoardLayerResponseDTO convertToDTO(BoardLayer layer) {
        BoardLayerResponseDTO dto = new BoardLayerResponseDTO();
        dto.setId(layer.getId());
        dto.setBoardId(layer.getBoardId());
        dto.setName(layer.getName());
        dto.setOrderIndex(layer.getOrderIndex());
        dto.setOpacity(layer.getOpacity());
        dto.setIsVisible(layer.getIsVisible());
        dto.setCreatedAt(layer.getCreatedAt());
        return dto;
    }

    private BoardLayer convertToEntity(BoardLayerRequestDTO dto) {
        BoardLayer layer = new BoardLayer();
        layer.setBoardId(dto.getBoardId());
        layer.setName(dto.getName());
        layer.setOrderIndex(dto.getOrderIndex());
        layer.setOpacity(dto.getOpacity());
        layer.setIsVisible(dto.getIsVisible());
        layer.setCreatedAt(LocalDateTime.now());
        return layer;
    }

    public List<BoardLayerResponseDTO> getAllLayers() {
        return boardLayerRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BoardLayerResponseDTO> getLayerById(Long id) {
        return boardLayerRepository.findById(id)
                .map(this::convertToDTO);
    }

    public BoardLayerResponseDTO createLayer(BoardLayerRequestDTO layerDTO) {
        BoardLayer layer = convertToEntity(layerDTO);
        BoardLayer savedLayer = boardLayerRepository.save(layer);
        return convertToDTO(savedLayer);
    }

    public Optional<BoardLayerResponseDTO> updateLayer(Long id, BoardLayerRequestDTO layerDTO) {
        Optional<BoardLayer> optionalLayer = boardLayerRepository.findById(id);
        
        if (optionalLayer.isEmpty()) {
            return Optional.empty();
        }

        BoardLayer layer = optionalLayer.get();
        layer.setBoardId(layerDTO.getBoardId());
        layer.setName(layerDTO.getName());
        layer.setOrderIndex(layerDTO.getOrderIndex());
        layer.setOpacity(layerDTO.getOpacity());
        layer.setIsVisible(layerDTO.getIsVisible());
        layer.setCreatedAt(LocalDateTime.now());

        BoardLayer updatedLayer = boardLayerRepository.save(layer);
        return Optional.of(convertToDTO(updatedLayer));
    }

    public boolean deleteLayer(Long id) {
        if (!boardLayerRepository.existsById(id)) {
            return false;
        }
        boardLayerRepository.deleteById(id);
        return true;
    }
    
    public List<BoardLayerResponseDTO> getLayersByBoardId(Long boardId) {
        return boardLayerRepository.findAll()
                .stream()
                .filter(layer -> layer.getBoardId().equals(boardId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}