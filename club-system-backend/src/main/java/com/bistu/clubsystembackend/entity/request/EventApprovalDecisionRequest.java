package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EventApprovalDecisionRequest {

    @NotBlank(message = "action is required")
    @Pattern(regexp = "^(APPROVE|REJECT)$", message = "action must be APPROVE or REJECT")
    private String action;

    @Size(max = 500, message = "rejectReason length must be <= 500")
    private String rejectReason;
}
