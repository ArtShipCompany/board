package com.example.artship.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.artship.board.dto.request.ActionHistoryRequestDTO;
import com.example.artship.board.dto.response.ActionHistoryResponseDTO;
import com.example.artship.board.model.ActionHistory;
import com.example.artship.board.repository.ActionHistoryRepository;

@Service
@Transactional
public class ActionHistoryService {

    @Autowired
    private ActionHistoryRepository actionHistoryRepository;

    private ActionHistoryResponseDTO convertToDTO(ActionHistory history) {
        ActionHistoryResponseDTO dto = new ActionHistoryResponseDTO();
        dto.setId(history.getId());
        dto.setBoardId(history.getBoardId());
        dto.setUserId(history.getUserId());
        dto.setActionType(history.getActionType());
        dto.setTargetType(history.getTargetType());
        dto.setTargetId(history.getTargetId());
        dto.setPreviousData(history.getPreviousData());
        dto.setNewData(history.getNewData());
        dto.setTimestamp(history.getTimestamp());
        dto.setSessionId(history.getSessionId());
        return dto;
    }

    private ActionHistory convertToEntity(ActionHistoryRequestDTO dto) {
        ActionHistory history = new ActionHistory();
        history.setBoardId(dto.getBoardId());
        history.setUserId(dto.getUserId());
        history.setActionType(dto.getActionType());
        history.setTargetType(dto.getTargetType());
        history.setTargetId(dto.getTargetId());
        history.setPreviousData(dto.getPreviousData());
        history.setNewData(dto.getNewData());
        history.setSessionId(dto.getSessionId());
        history.setTimestamp(LocalDateTime.now());
        return history;
    }

    public List<ActionHistoryResponseDTO> getAllHistory() {
        return actionHistoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ActionHistoryResponseDTO> getHistoryById(Long id) {
        return actionHistoryRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<ActionHistoryResponseDTO> getHistoryByBoardId(Long boardId) {
        return actionHistoryRepository.findByBoardIdOrderByTimestampDesc(boardId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ActionHistoryResponseDTO> getHistoryByUserId(Long userId) {
        return actionHistoryRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ActionHistoryResponseDTO> getHistoryByTarget(Long boardId, Long targetId) {
        return actionHistoryRepository.findByBoardIdAndTargetIdOrderByTimestampDesc(boardId, targetId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ActionHistoryResponseDTO createAction(ActionHistoryRequestDTO dto) {
        ActionHistory history = convertToEntity(dto);
        ActionHistory savedHistory = actionHistoryRepository.save(history);
        return convertToDTO(savedHistory);
    }

    public boolean deleteHistory(Long id) {
        if (!actionHistoryRepository.existsById(id)) {
            return false;
        }
        actionHistoryRepository.deleteById(id);
        return true;
    }

    public void deleteHistoryByBoardId(Long boardId) {
        actionHistoryRepository.deleteByBoardId(boardId);
    }

    public void deleteHistoryBySessionId(Long sessionId) {
        actionHistoryRepository.deleteBySessionId(sessionId);
    }

    public ActionHistoryResponseDTO getLatestAction(Long boardId) {
        List<ActionHistory> history = actionHistoryRepository.findByBoardIdOrderByTimestampDesc(boardId);
        if (history.isEmpty()) {
            return null;
        }
        return convertToDTO(history.get(0));
    }
}