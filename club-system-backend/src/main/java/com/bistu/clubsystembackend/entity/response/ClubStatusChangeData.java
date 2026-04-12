package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ClubStatusChangeData {
    private Long clubId;
    private String status;
    private LocalDateTime updatedAt;
}
