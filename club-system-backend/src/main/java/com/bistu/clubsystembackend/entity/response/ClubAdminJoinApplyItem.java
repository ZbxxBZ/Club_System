package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubAdminJoinApplyItem {
    private Long id;
    private Long clubId;
    private Long userId;
    private String username;
    private String realName;
    private String joinStatus;
    private String selfIntro;
    private String applyReason;
    private String rejectReason;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
}
