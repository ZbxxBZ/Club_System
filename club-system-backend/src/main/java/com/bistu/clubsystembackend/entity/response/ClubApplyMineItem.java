package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubApplyMineItem {
    private Long id;
    private Long clubId;
    private String clubName;
    private Integer applyStatus;
    private LocalDateTime createdAt;
}
