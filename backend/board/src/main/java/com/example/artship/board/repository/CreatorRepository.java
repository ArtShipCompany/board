package com.example.artship.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.artship.board.model.Creator;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {
}