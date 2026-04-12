package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

@Data
public class UserProfileData {
    private String username;
    private String realName;
    private String studentNo;
    private String phone;
    private String email;
}
