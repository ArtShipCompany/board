
package com.example.artship.board.repository;

import com.example.artship.board.model.BoardLayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardLayerRepository extends JpaRepository<BoardLayer, Long> {
}