package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AnomalyExpenseItem {
    private Long id;
    private Long clubId;
    private String clubName;
    private BigDecimal amount;
    private String category;
    private String expenseDesc;
    private Integer approveLevel;
    private Integer expenseStatus;
    private String invoiceUrl;
    private String rejectReason;
    private String anomalyReason;
    private LocalDateTime createdAt;
}
