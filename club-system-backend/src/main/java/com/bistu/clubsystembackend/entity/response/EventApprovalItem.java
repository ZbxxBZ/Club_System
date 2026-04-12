package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventApprovalItem {
    private Long id;
    private Long clubId;
    private String clubName;
    private String title;
    private String location;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer limitCount;
    private Integer eventStatus;
    private LocalDateTime createdAt;
}
