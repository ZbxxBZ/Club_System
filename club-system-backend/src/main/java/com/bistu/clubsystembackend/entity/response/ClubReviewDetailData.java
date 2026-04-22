package com.bistu.clubsystembackend.entity.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClubReviewDetailData {
    private Long id;
    private Long clubId;
    private String clubName;
    private Integer reviewYear;
    private Integer reviewStatus;
    private String summaryText;
    private String attachmentUrl;
    private BigDecimal score;
    private String rejectReason;

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private Integer memberCount;
    private Integer eventCount;

    private List<IncomeDetailData> incomeList;
    private List<ExpenseDetailData> expenseList;
    private List<ClubMemberItem> memberList;

    private Boolean reviewWindowOpen;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
