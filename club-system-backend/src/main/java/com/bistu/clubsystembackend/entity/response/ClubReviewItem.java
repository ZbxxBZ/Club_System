package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClubReviewItem {
    private Long id;
    private Long clubId;
    private String clubName;
    private Integer reviewYear;
    private Integer reviewStatus;
    private BigDecimal score;
    private String rejectReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
