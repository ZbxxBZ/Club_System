package com.bistu.clubsystembackend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubJoinEligibilityMeta {
    private Long id;
    private String recruitStatus;
    private LocalDateTime recruitStartAt;
    private LocalDateTime recruitEndAt;
    private Integer recruitLimit;
    private Integer activeMemberCount;
    private Integer pendingApplyCount;
    private Integer approved;
}
