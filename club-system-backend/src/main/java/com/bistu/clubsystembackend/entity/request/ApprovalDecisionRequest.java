package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApprovalDecisionRequest {
    @NotBlank
    private String action;
    private String opinion;
}
