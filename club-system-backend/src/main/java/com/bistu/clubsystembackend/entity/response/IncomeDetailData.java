package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class IncomeDetailData {
    private Long id;
    private Long clubId;
    private Integer incomeType;
    private String incomeTypeName;
    private BigDecimal amount;
    private LocalDateTime occurAt;
    private String proofUrl;
    private String sourceDesc;
    private LocalDateTime createdAt;
}
