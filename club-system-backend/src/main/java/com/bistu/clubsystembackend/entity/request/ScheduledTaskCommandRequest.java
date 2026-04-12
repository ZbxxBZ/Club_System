package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScheduledTaskCommandRequest {
    @NotBlank
    private String action;
    private String reason;
}
