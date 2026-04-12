package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubAdminEventItem {
    private Long id;
    private String title;
    private String location;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer limitCount;
    private Integer signupCount;
    private Integer checkinCount;
    private Integer eventStatus;
    private String rejectReason;
    private Boolean hasSummary;
    private LocalDateTime createdAt;
}
