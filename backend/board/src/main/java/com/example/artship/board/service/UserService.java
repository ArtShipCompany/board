package com.example.artship.board.service;

import com.example.artship.board.dto.request.UserRequestDTO;
import com.example.artship.board.dto.response.UserResponseDTO;
import com.example.artship.board.model.User;
import com.example.artship.board.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private UserResponseDTO convertToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    private User convertToEntity(UserRequestDTO dto) {
        User user = new User();
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    public UserResponseDTO createUser(UserRequestDTO userDTO) {
        User user = convertToEntity(userDTO);
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public Optional<UserResponseDTO> updateUser(Long id, UserRequestDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(id);
        
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        User user = optionalUser.get();
        user.setNickname(userDTO.getNickname());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setCreatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        return Optional.of(convertToDTO(updatedUser));
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
    
    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .map(this::convertToDTO);
    }
    
    public Optional<UserResponseDTO> getUserByNickname(String nickname) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getNickname().equalsIgnoreCase(nickname))
                .findFirst()
                .map(this::convertToDTO);
    }
}