package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventSignupItem {
    private Long id;
    private Long userId;
    private String realName;
    private String studentNo;
    private Integer signupStatus;
    private LocalDateTime signupAt;
    private Boolean checkedIn;
    private LocalDateTime checkinAt;
}
