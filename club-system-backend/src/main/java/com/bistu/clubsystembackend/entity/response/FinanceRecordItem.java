package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FinanceRecordItem {
    private Long id;
    private String type;
    private String typeName;
    private BigDecimal amount;
    private String status;
    private LocalDateTime occurAt;
}
