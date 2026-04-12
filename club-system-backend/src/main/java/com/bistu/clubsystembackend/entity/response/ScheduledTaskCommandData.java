package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ScheduledTaskCommandData {
    private String taskCode;
    private String action;
    private String taskStatus;
    private String jobId;
    private Long processedCount;
    private LocalDateTime updatedAt;
}
