package com.example.artship.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.artship.board.model.FeedbackRequest;

@Repository
public interface FeedbackRequestRepository extends JpaRepository<FeedbackRequest, Long> {
    List<FeedbackRequest> findByUserId(Long userId);
    List<FeedbackRequest> findByStatus(String status);
    List<FeedbackRequest> findByRequestType(String requestType);
    List<FeedbackRequest> findByUserIdAndStatus(Long userId, String status);
    List<FeedbackRequest> findByPriorityOrderByCreatedAtDesc(String priority);
}