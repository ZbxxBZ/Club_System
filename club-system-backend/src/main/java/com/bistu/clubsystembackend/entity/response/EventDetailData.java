package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDetailData {
    private Long id;
    private Long clubId;
    private String clubName;
    private String title;
    private String content;
    private String location;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime signupStartAt;
    private LocalDateTime signupEndAt;
    private Integer limitCount;
    private Boolean onlyMember;
    private Integer eventStatus;
    private String safetyPlanUrl;
    private String checkinCode;
    private String rejectReason;
    private Integer signupCount;
    private Integer checkinCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
