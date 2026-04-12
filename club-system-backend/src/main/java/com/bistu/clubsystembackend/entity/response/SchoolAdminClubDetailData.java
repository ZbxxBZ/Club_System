package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SchoolAdminClubDetailData {
    private Long clubId;
    private String clubName;
    private String category;
    private Integer status;
    private Integer applyStatus;
    private String initiatorRealName;
    private String instructorName;
    private String purpose;
    private String charterUrl;
    private String instructorProofUrl;
    private LocalDateTime createdAt;
}
