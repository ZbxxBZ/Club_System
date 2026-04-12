package com.bistu.clubsystembackend.entity.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateExpenseRequest {

    private Long eventId;

    @Size(max = 50, message = "category length must be <= 50")
    private String category;

    @NotNull(message = "amount is required")
    private BigDecimal amount;

    @Size(max = 100, message = "payeeName length must be <= 100")
    private String payeeName;

    @Size(max = 100, message = "payeeAccount length must be <= 100")
    private String payeeAccount;

    @Size(max = 255, message = "invoiceUrl length must be <= 255")
    private String invoiceUrl;

    @Size(max = 500, message = "expenseDesc length must be <= 500")
    private String expenseDesc;
}
