package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthMeResponseData {
    private Long userId;
    private String username;
    private String realName;
    private String permissionType;
    private String status;
    private Long clubId;
    private List<String> permissions;
}
