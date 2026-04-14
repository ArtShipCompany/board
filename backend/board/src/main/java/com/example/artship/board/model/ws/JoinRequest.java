package com.example.artship.board.model.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequest {
    private String visitorId;
    private String roomId;
    private String visitorName;
    private String color;
}