package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateIncomeRequest {

    @NotNull(message = "incomeType is required")
    private Integer incomeType;

    @NotNull(message = "amount is required")
    private BigDecimal amount;

    @NotNull(message = "occurAt is required")
    private LocalDateTime occurAt;

    @NotBlank(message = "proofUrl is required")
    @Size(max = 255, message = "proofUrl length must be <= 255")
    private String proofUrl;

    @Size(max = 255, message = "sourceDesc length must be <= 255")
    private String sourceDesc;
}
