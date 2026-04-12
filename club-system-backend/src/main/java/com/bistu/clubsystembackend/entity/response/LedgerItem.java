package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LedgerItem {
    private Long id;
    private Long clubId;
    private Integer bizType;
    private String bizTypeName;
    private Long bizId;
    private BigDecimal changeAmount;
    private BigDecimal balanceAfter;
    private LocalDateTime occurAt;
}
