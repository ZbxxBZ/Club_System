package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClubPublicItem {
    private Long id;
    private String clubName;
    private String category;
    private String instructorName;
    private String purpose;
    private LocalDate establishedDate;
    private String recruitStatus;
    private LocalDateTime recruitStartAt;
    private LocalDateTime recruitEndAt;
    private Integer recruitLimit;
    private Integer signedUpCount;
    private String joinStatus;
    private Boolean joinEntryEnabled;
    private Boolean isRecruiting;
    private String status;
}
