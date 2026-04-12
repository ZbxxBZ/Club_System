package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventOpenItem {
    private Long id;
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
    private Integer remainingSlots;
    private Integer eventStatus;
    private Integer signupStatus;
    private Boolean checkedIn;
    private Boolean canSignup;
}
