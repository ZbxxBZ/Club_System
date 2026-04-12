package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubCancelDetailData {
    private Long id;
    private Long clubId;
    private String clubName;
    private Integer applyType;
    private String applyReason;
    private String assetSettlementUrl;
    private Integer cancelStatus;
    private String initiatorRealName;
    private LocalDateTime createdAt;
}
