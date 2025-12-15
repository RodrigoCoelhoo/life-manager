package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.service.DashboardService;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthNetBalanceResponseDTO(
        YearMonth yearMonth,
        BigDecimal income,
        BigDecimal expenses,
        BigDecimal netBalance
) {
    public static MonthNetBalanceResponseDTO fromEntities(YearMonth yearMonth, DashboardService.Netbalance netBalance) {
        return new MonthNetBalanceResponseDTO(
                yearMonth,
                netBalance.getIncome(),
                netBalance.getExpenses().negate(),
                netBalance.getIncome().subtract(netBalance.getExpenses())
        );
    }
}
