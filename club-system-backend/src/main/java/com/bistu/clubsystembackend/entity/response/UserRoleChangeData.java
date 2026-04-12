package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRoleChangeData {
    private Long userId;
    private String roleCode;
    private String effectiveRoleCode;
}
