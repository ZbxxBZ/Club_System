package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseData {
    private String token;
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String username;
    private String realName;
    private String permissionType;
    private String status;
    private Long clubId;
}
