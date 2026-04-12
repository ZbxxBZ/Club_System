package com.bistu.clubsystembackend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthUser {
    private Long id;
    private String username;
    private String passwordHash;
    private String realName;
    private String studentNo;
    private String phone;
    private String email;
    private Integer status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
}
