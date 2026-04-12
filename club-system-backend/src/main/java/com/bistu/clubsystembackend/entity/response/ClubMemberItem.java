package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClubMemberItem {
    private Long id;
    private Long clubId;
    private Long userId;
    private String username;
    private String realName;
    private Long positionId;
    private String positionName;
    private Integer memberStatus;
    private LocalDateTime joinAt;
    private LocalDateTime quitAt;
    private BigDecimal contributionScore;
}
