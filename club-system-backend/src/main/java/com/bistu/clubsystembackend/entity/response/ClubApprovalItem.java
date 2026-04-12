package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubApprovalItem {
    private Long id;
    private Long clubId;
    private Long initiatorUserId;
    private String clubName;
    private Integer applyStatus;
    private String initiatorRealName;
    private LocalDateTime createdAt;
}
