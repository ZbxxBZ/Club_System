package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatusChangeData {
    private Long userId;
    private String status;
}
