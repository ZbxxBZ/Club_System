package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApprovalStatusUpdateRequest {
    @NotNull
    @Min(2)
    @Max(5)
    private Integer applyStatus;

    @Size(max = 500)
    private String opinion;
}
