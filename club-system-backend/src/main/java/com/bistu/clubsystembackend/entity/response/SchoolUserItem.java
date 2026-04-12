package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SchoolUserItem {
    private Long id;
    private String username;
    private String realName;
    private String roleCode;
    private String status;
    private LocalDateTime graduateAt;
}
