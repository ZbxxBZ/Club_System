package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReviewDecisionRequest {
    @NotBlank(message = "操作不能为空")
    private String action;

    private BigDecimal score;

    @Size(max = 500)
    private String rejectReason;
}
