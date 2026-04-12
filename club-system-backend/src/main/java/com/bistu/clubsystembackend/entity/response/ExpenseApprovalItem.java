package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExpenseApprovalItem {
    private Long id;
    private Long clubId;
    private String clubName;
    private BigDecimal amount;
    private String category;
    private String expenseDesc;
    private Integer expenseStatus;
    private String invoiceUrl;
    private LocalDateTime createdAt;
}
