package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

@Data
public class ClubInfoData {
    private Long id;
    private String clubName;
    private String category;
    private String introduction;
    private String purpose;
    private String instructorName;
}
