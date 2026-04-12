package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FinanceAuditReportData {
    private Long clubId;
    private String clubName;
    private Integer year;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private List<MonthlyFinanceStat> monthlyStats;
    private List<CategoryExpenseStat> categoryStats;
    private Long anomalyCount;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyFinanceStat {
        private Integer month;
        private BigDecimal income;
        private BigDecimal expense;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryExpenseStat {
        private String category;
        private BigDecimal amount;
    }
}
