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

import com.example.artship.board.dto.request.UserRequestDTO;
import com.example.artship.board.dto.response.UserResponseDTO;
import com.example.artship.board.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "API для управления пользователями")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Получить всех пользователей", 
               description = "Возвращает список всех зарегистрированных пользователей")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка пользователей")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Получить пользователя по ID", 
               description = "Возвращает пользователя по указанному идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь найден"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id) {
        Optional<UserResponseDTO> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить пользователя по email", 
               description = "Возвращает пользователя по адресу электронной почты")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь найден"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(
            @Parameter(description = "Email пользователя", example = "user@example.com", required = true)
            @PathVariable String email) {
        Optional<UserResponseDTO> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить пользователя по никнейму", 
               description = "Возвращает пользователя по никнейму (имени пользователя)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь найден"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<UserResponseDTO> getUserByNickname(
            @Parameter(description = "Никнейм пользователя", example = "artist123", required = true)
            @PathVariable String nickname) {
        Optional<UserResponseDTO> user = userService.getUserByNickname(nickname);
        return user.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать нового пользователя", 
               description = "Регистрирует нового пользователя в системе")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
        @ApiResponse(responseCode = "409", description = "Пользователь с таким email или никнеймом уже существует")
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Parameter(description = "Данные для регистрации пользователя", required = true)
            @Valid @RequestBody UserRequestDTO userDTO) {
        UserResponseDTO savedUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @Operation(summary = "Обновить пользователя", 
               description = "Обновляет информацию о существующем пользователе")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
        @ApiResponse(responseCode = "409", description = "Пользователь с таким email или никнеймом уже существует"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен (не свой профиль)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные пользователя", required = true)
            @Valid @RequestBody UserRequestDTO userDTO) {
        Optional<UserResponseDTO> updatedUser = userService.updateUser(id, userDTO);
        return updatedUser.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить пользователя", 
               description = "Удаляет пользователя по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен (не свой профиль или админ)")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}