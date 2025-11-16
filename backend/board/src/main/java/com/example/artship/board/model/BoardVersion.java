package com.example.artship.board.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "board_versions")
public class BoardVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "ID доски обязателен")
    @Column(name = "board_id")
    private Long boardId;

    @NotNull(message = "ID пользователя обязателен")
    @Column(name = "user_id")
    private Long userId;

    @NotNull(message = "Номер версии обязателен")
    @Column(name = "version_number")
    private Integer versionNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "snapshot_data")
    private String snapshotData;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBoardId() { return boardId; }
    public void setBoardId(Long boardId) { this.boardId = boardId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSnapshotData() { return snapshotData; }
    public void setSnapshotData(String snapshotData) { this.snapshotData = snapshotData; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}