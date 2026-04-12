package com.bistu.clubsystembackend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthSessionInfo {
    private Long userId;
    private String username;
    private String realName;
    private Integer status;
    private LocalDateTime graduateAt;
    private String roleCode;
    private Long clubId;
}
