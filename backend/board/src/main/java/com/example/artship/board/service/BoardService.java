package com.example.artship.board.service;

import com.example.artship.board.dto.request.BoardRequestDTO;
import com.example.artship.board.dto.response.BoardResponseDTO;
import com.example.artship.board.model.Board;
import com.example.artship.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    private BoardResponseDTO convertToDTO(Board board) {
        BoardResponseDTO dto = new BoardResponseDTO();
        dto.setId(board.getId());
        dto.setDescription(board.getDescription());
        dto.setWidth(board.getWidth());
        dto.setHeight(board.getHeight());
        dto.setBackgroundColor(board.getBackground_color());
        dto.setBackgroundImage(board.getBackground_image());
        dto.setCreatedAt(board.getCreated_at());
        dto.setUpdatedAt(board.getUpdated_at());
        return dto;
    }

    private Board convertToEntity(BoardRequestDTO dto) {
        Board board = new Board();
        board.setDescription(dto.getDescription());
        board.setWidth(dto.getWidth());
        board.setHeight(dto.getHeight());
        board.setBackground_color(dto.getBackgroundColor());
        board.setBackground_image(dto.getBackgroundImage());
        
        LocalDateTime now = LocalDateTime.now();
        board.setCreated_at(now);
        board.setUpdated_at(now);
        
        return board;
    }

    public List<BoardResponseDTO> getAllBoards() {
        return boardRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BoardResponseDTO> getBoardById(Long id) {
        return boardRepository.findById(id)
                .map(this::convertToDTO);
    }

    public BoardResponseDTO createBoard(BoardRequestDTO boardDTO) {
        Board board = convertToEntity(boardDTO);
        Board savedBoard = boardRepository.save(board);
        return convertToDTO(savedBoard);
    }

    public Optional<BoardResponseDTO> updateBoard(Long id, BoardRequestDTO boardDTO) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        
        if (optionalBoard.isEmpty()) {
            return Optional.empty();
        }

        Board board = optionalBoard.get();
        board.setDescription(boardDTO.getDescription());
        board.setWidth(boardDTO.getWidth());
        board.setHeight(boardDTO.getHeight());
        board.setBackground_color(boardDTO.getBackgroundColor());
        board.setBackground_image(boardDTO.getBackgroundImage());
        board.setUpdated_at(LocalDateTime.now());

        Board updatedBoard = boardRepository.save(board);
        return Optional.of(convertToDTO(updatedBoard));
    }

    public boolean deleteBoard(Long id) {
        if (!boardRepository.existsById(id)) {
            return false;
        }
        boardRepository.deleteById(id);
        return true;
    }
}