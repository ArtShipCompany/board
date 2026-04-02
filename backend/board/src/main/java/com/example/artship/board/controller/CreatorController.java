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
import org.springframework.web.bind.annotation.RestController;

import com.example.artship.board.dto.request.CreatorRequestDTO;
import com.example.artship.board.dto.response.CreatorResponseDTO;
import com.example.artship.board.service.CreatorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/creators")
@Tag(name = "Creator Controller", description = "API для управления создателями")
public class CreatorController {

    @Autowired
    private CreatorService creatorService;

    @Operation(summary = "Получить всех создателей", description = "Возвращает список всех создателей")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка создателей")
    @GetMapping
    public ResponseEntity<List<CreatorResponseDTO>> getAllCreators() {
        List<CreatorResponseDTO> creators = creatorService.getAllCreators();
        return ResponseEntity.ok(creators);
    }

    @Operation(summary = "Получить создателя по ID", description = "Возвращает создателя по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Создатель найден"),
        @ApiResponse(responseCode = "404", description = "Создатель не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CreatorResponseDTO> getCreatorById(
            @Parameter(description = "ID создателя", required = true)
            @PathVariable Long id) {
        
        Optional<CreatorResponseDTO> creator = creatorService.getCreatorById(id);
        return creator.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать нового создателя", description = "Создает нового создателя с указанными параметрами")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Создатель успешно создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PostMapping
    public ResponseEntity<CreatorResponseDTO> createCreator(
            @Parameter(description = "Данные для создания создателя", required = true)
            @Valid @RequestBody CreatorRequestDTO creatorDTO) {
        
        CreatorResponseDTO savedCreator = creatorService.createCreator(creatorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCreator);
    }

    @Operation(summary = "Обновить создателя", description = "Обновляет существующего создателя по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Создатель успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Создатель не найден"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CreatorResponseDTO> updateCreator(
            @Parameter(description = "ID создателя", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные создателя", required = true)
            @Valid @RequestBody CreatorRequestDTO creatorDTO) {
        
        Optional<CreatorResponseDTO> updatedCreator = creatorService.updateCreator(id, creatorDTO);
        return updatedCreator.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить создателя", description = "Удаляет создателя по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Создатель успешно удален"),
        @ApiResponse(responseCode = "404", description = "Создатель не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCreator(
            @Parameter(description = "ID создателя", required = true)
            @PathVariable Long id) {
        
        boolean deleted = creatorService.deleteCreator(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}