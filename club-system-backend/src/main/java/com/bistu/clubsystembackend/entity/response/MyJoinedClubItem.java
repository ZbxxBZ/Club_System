package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyJoinedClubItem {
    private Long clubId;
    private String clubName;
    private String category;
    private String purpose;
    private String positionName;
    private LocalDateTime joinAt;
}
