package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubCancelApplyData {
    private Long id;
    private Long clubId;
    private Integer applyType;
    private String applyReason;
    private String assetSettlementUrl;
    private Integer cancelStatus;
    private LocalDateTime createdAt;
}
