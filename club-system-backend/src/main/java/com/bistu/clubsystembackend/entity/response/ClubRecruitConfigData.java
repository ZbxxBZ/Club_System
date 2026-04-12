package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubRecruitConfigData {
    private Long clubId;
    private LocalDateTime recruitStartAt;
    private LocalDateTime recruitEndAt;
    private Integer recruitLimit;
    private String recruitStatus;
    private LocalDateTime updatedAt;
}
