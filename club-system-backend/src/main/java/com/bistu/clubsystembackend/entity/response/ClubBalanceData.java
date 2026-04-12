package com.bistu.clubsystembackend.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ClubBalanceData {
    private Long clubId;
    private BigDecimal balance;
    private BigDecimal pendingExpense;
    private BigDecimal availableBalance;

    public ClubBalanceData(Long clubId, BigDecimal balance, BigDecimal pendingExpense) {
        this.clubId = clubId;
        this.balance = balance;
        this.pendingExpense = pendingExpense;
        this.availableBalance = balance.subtract(pendingExpense);
    }
}
