package com.bistu.clubsystembackend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventSignupMeta {
    private Long id;
    private Long clubId;
    private Integer limitCount;
    private Integer onlyMember;
    private Integer eventStatus;
    private LocalDateTime signupStartAt;
    private LocalDateTime signupEndAt;
}
