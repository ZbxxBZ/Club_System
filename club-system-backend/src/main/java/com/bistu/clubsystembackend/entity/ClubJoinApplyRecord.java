package com.bistu.clubsystembackend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubJoinApplyRecord {
    private Long id;
    private Long clubId;
    private Long userId;
    private Integer joinStatus;
    private String applyReason;
    private String rejectReason;
    private Long reviewUserId;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
}
