package com.example.artship.board.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.artship.board.dto.request.FeedbackRequestRequestDTO;
import com.example.artship.board.dto.response.FeedbackRequestResponseDTO;
import com.example.artship.board.service.FeedbackRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/feedback-requests")
@Tag(name = "Feedback Request Controller", description = "API для запросов обратной связи")
public class FeedbackRequestController {

    @Autowired
    private FeedbackRequestService feedbackRequestService;

    @Operation(summary = "Получить все запросы", description = "Возвращает все запросы обратной связи")
    @ApiResponse(responseCode = "200", description = "Успешное получение")
    @GetMapping
    public ResponseEntity<List<FeedbackRequestResponseDTO>> getAllRequests() {
        List<FeedbackRequestResponseDTO> requests = feedbackRequestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Получить запрос по ID", description = "Возвращает запрос по идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Запрос найден"),
        @ApiResponse(responseCode = "404", description = "Запрос не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackRequestResponseDTO> getRequestById(
            @Parameter(description = "ID запроса", required = true)
            @PathVariable Long id) {
        
        Optional<FeedbackRequestResponseDTO> request = feedbackRequestService.getRequestById(id);
        return request.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить запросы пользователя", description = "Возвращает все запросы пользователя")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FeedbackRequestResponseDTO>> getRequestsByUserId(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId) {
        
        List<FeedbackRequestResponseDTO> requests = feedbackRequestService.getRequestsByUserId(userId);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Получить запросы по статусу", description = "Возвращает запросы с указанным статусом")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<FeedbackRequestResponseDTO>> getRequestsByStatus(
            @Parameter(description = "Статус", required = true)
            @PathVariable String status) {
        
        List<FeedbackRequestResponseDTO> requests = feedbackRequestService.getRequestsByStatus(status);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Получить запросы по типу", description = "Возвращает запросы указанного типа")
    @GetMapping("/type/{requestType}")
    public ResponseEntity<List<FeedbackRequestResponseDTO>> getRequestsByType(
            @Parameter(description = "Тип запроса", required = true)
            @PathVariable String requestType) {
        
        List<FeedbackRequestResponseDTO> requests = feedbackRequestService.getRequestsByType(requestType);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Получить запросы по приоритету", description = "Возвращает запросы с указанным приоритетом")
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<FeedbackRequestResponseDTO>> getRequestsByPriority(
            @Parameter(description = "Приоритет", required = true)
            @PathVariable String priority) {
        
        List<FeedbackRequestResponseDTO> requests = feedbackRequestService.getRequestsByPriority(priority);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Создать запрос", description = "Создает новый запрос обратной связи")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Запрос создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping
    public ResponseEntity<FeedbackRequestResponseDTO> createRequest(
            @Parameter(description = "Данные запроса", required = true)
            @Valid @RequestBody FeedbackRequestRequestDTO dto) {
        
        FeedbackRequestResponseDTO savedRequest = feedbackRequestService.createRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
    }

    @Operation(summary = "Обновить запрос", description = "Обновляет существующий запрос")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Запрос обновлен"),
        @ApiResponse(responseCode = "404", description = "Запрос не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackRequestResponseDTO> updateRequest(
            @Parameter(description = "ID запроса", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные", required = true)
            @Valid @RequestBody FeedbackRequestRequestDTO dto) {
        
        Optional<FeedbackRequestResponseDTO> updatedRequest = feedbackRequestService.updateRequest(id, dto);
        return updatedRequest.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Добавить ответ админа", description = "Добавляет ответ администратора на запрос")
    @PutMapping("/{id}/admin-response")
    public ResponseEntity<FeedbackRequestResponseDTO> addAdminResponse(
            @Parameter(description = "ID запроса", required = true)
            @PathVariable Long id,
            @Parameter(description = "Ответ администратора", required = true)
            @RequestParam String adminResponse) {
        
        Optional<FeedbackRequestResponseDTO> updatedRequest = feedbackRequestService.addAdminResponse(id, adminResponse);
        return updatedRequest.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Обновить статус запроса", description = "Изменяет статус запроса")
    @PutMapping("/{id}/status")
    public ResponseEntity<FeedbackRequestResponseDTO> updateStatus(
            @Parameter(description = "ID запроса", required = true)
            @PathVariable Long id,
            @Parameter(description = "Новый статус", required = true)
            @RequestParam String status) {
        
        Optional<FeedbackRequestResponseDTO> updatedRequest = feedbackRequestService.updateStatus(id, status);
        return updatedRequest.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить запрос", description = "Удаляет запрос по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Удалено"),
        @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(
            @Parameter(description = "ID запроса", required = true)
            @PathVariable Long id) {
        
        boolean deleted = feedbackRequestService.deleteRequest(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}