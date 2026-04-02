package com.example.artship.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.artship.board.dto.request.FeedbackRequestRequestDTO;
import com.example.artship.board.dto.response.FeedbackRequestResponseDTO;
import com.example.artship.board.model.FeedbackRequest;
import com.example.artship.board.repository.FeedbackRequestRepository;

@Service
@Transactional
public class FeedbackRequestService {

    @Autowired
    private FeedbackRequestRepository feedbackRequestRepository;

    private FeedbackRequestResponseDTO convertToDTO(FeedbackRequest request) {
        FeedbackRequestResponseDTO dto = new FeedbackRequestResponseDTO();
        dto.setId(request.getId());
        dto.setUserId(request.getUserId());
        dto.setRequestType(request.getRequestType());
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setPriority(request.getPriority());
        dto.setStatus(request.getStatus());
        dto.setAdminResponse(request.getAdminResponse());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        return dto;
    }

    private FeedbackRequest convertToEntity(FeedbackRequestRequestDTO dto) {
        FeedbackRequest request = new FeedbackRequest();
        request.setUserId(dto.getUserId());
        request.setRequestType(dto.getRequestType());
        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setPriority(dto.getPriority() != null ? dto.getPriority() : "normal");
        request.setStatus(dto.getStatus() != null ? dto.getStatus() : "pending");
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return request;
    }

    public List<FeedbackRequestResponseDTO> getAllRequests() {
        return feedbackRequestRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<FeedbackRequestResponseDTO> getRequestById(Long id) {
        return feedbackRequestRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<FeedbackRequestResponseDTO> getRequestsByUserId(Long userId) {
        return feedbackRequestRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FeedbackRequestResponseDTO> getRequestsByStatus(String status) {
        return feedbackRequestRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FeedbackRequestResponseDTO> getRequestsByType(String requestType) {
        return feedbackRequestRepository.findByRequestType(requestType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FeedbackRequestResponseDTO> getRequestsByPriority(String priority) {
        return feedbackRequestRepository.findByPriorityOrderByCreatedAtDesc(priority)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FeedbackRequestResponseDTO createRequest(FeedbackRequestRequestDTO dto) {
        FeedbackRequest request = convertToEntity(dto);
        FeedbackRequest savedRequest = feedbackRequestRepository.save(request);
        return convertToDTO(savedRequest);
    }

    public Optional<FeedbackRequestResponseDTO> updateRequest(Long id, FeedbackRequestRequestDTO dto) {
        Optional<FeedbackRequest> optionalRequest = feedbackRequestRepository.findById(id);
        
        if (optionalRequest.isEmpty()) {
            return Optional.empty();
        }

        FeedbackRequest request = optionalRequest.get();
        if (dto.getTitle() != null) {
            request.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            request.setDescription(dto.getDescription());
        }
        if (dto.getPriority() != null) {
            request.setPriority(dto.getPriority());
        }
        if (dto.getStatus() != null) {
            request.setStatus(dto.getStatus());
        }
        request.setUpdatedAt(LocalDateTime.now());

        FeedbackRequest updatedRequest = feedbackRequestRepository.save(request);
        return Optional.of(convertToDTO(updatedRequest));
    }

    public Optional<FeedbackRequestResponseDTO> addAdminResponse(Long id, String adminResponse) {
        Optional<FeedbackRequest> optionalRequest = feedbackRequestRepository.findById(id);
        
        if (optionalRequest.isEmpty()) {
            return Optional.empty();
        }

        FeedbackRequest request = optionalRequest.get();
        request.setAdminResponse(adminResponse);
        request.setStatus("in_review");
        request.setUpdatedAt(LocalDateTime.now());

        FeedbackRequest updatedRequest = feedbackRequestRepository.save(request);
        return Optional.of(convertToDTO(updatedRequest));
    }

    public Optional<FeedbackRequestResponseDTO> updateStatus(Long id, String status) {
        Optional<FeedbackRequest> optionalRequest = feedbackRequestRepository.findById(id);
        
        if (optionalRequest.isEmpty()) {
            return Optional.empty();
        }

        FeedbackRequest request = optionalRequest.get();
        request.setStatus(status);
        request.setUpdatedAt(LocalDateTime.now());

        FeedbackRequest updatedRequest = feedbackRequestRepository.save(request);
        return Optional.of(convertToDTO(updatedRequest));
    }

    public boolean deleteRequest(Long id) {
        if (!feedbackRequestRepository.existsById(id)) {
            return false;
        }
        feedbackRequestRepository.deleteById(id);
        return true;
    }
}