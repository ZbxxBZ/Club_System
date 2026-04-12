package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JoinApplyDecisionRequest {

    @NotBlank(message = "decision is required")
    @Pattern(regexp = "^(APPROVE|REJECT)$", message = "decision must be APPROVE or REJECT")
    private String decision;

    @Size(max = 500, message = "rejectReason length must be <= 500")
    private String rejectReason;
}
