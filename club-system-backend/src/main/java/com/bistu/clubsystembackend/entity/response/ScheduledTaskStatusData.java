package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ScheduledTaskStatusData {
    private String taskCode;
    private String taskStatus;
    private LocalDateTime updatedAt;
}
