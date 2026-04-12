package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FinanceStatisticsData {
    private BigDecimal totalSchoolFunding;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalBalance;
    private List<IncomeByType> incomeByType;
    private List<ClubFinanceItem> clubFinanceList;
    private List<MonthlyFinance> monthlyFinanceStats;

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class IncomeByType {
        private String typeName;
        private BigDecimal amount;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class ClubFinanceItem {
        private String clubName;
        private BigDecimal income;
        private BigDecimal expense;
        private BigDecimal balance;
        private int eventCount;
        private BigDecimal efficiency;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class MonthlyFinance {
        private int month;
        private BigDecimal income;
        private BigDecimal expense;
    }
}
