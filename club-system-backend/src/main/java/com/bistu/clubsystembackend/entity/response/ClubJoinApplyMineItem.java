package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubJoinApplyMineItem {
    private Long id;
    private Long clubId;
    private String clubName;
    private String joinStatus;
    private String selfIntro;
    private String applyReason;
    private String rejectReason;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private String positionName;
    private Boolean isActiveMember;
}
