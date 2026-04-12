package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExpenseDetailData {
    private Long id;
    private Long clubId;
    private String clubName;
    private Long eventId;
    private String category;
    private BigDecimal amount;
    private String payeeName;
    private String payeeAccount;
    private String invoiceUrl;
    private String expenseDesc;
    private Integer approveLevel;
    private Integer expenseStatus;
    private String rejectReason;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
