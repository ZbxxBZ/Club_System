package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubApprovalDetailData {
    private Long id;
    private Long clubId;
    private String clubName;
    private String category;
    private Integer applyStatus;
    private String initiatorRealName;
    private String instructorName;
    private String purpose;
    private String remark;
    private String charterUrl;
    private String instructorProofUrl;
    private LocalDateTime createdAt;
}
