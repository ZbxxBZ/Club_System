package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubPositionItem {
    private Long id;
    private Long clubId;
    private String positionName;
    private Long parentPositionId;
    private Integer levelNo;
    private Integer sortNo;
    private LocalDateTime createdAt;
}
