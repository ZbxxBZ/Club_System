package com.bistu.clubsystembackend.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentUser {
    private Long userId;
    private String username;
    private String realName;
    private String roleCode;
    private Long clubId;
    private String status;
}
