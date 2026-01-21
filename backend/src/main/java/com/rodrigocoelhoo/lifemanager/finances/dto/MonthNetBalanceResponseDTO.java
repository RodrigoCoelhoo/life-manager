package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.service.DashboardService;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthNetBalanceResponseDTO(
        YearMonth yearMonth,
        BigDecimal income,
        BigDecimal expenses,
        BigDecimal netBalance
) implements Serializable {
    public static MonthNetBalanceResponseDTO fromEntities(YearMonth yearMonth, DashboardService.Netbalance netBalance) {
        return new MonthNetBalanceResponseDTO(
                yearMonth,
                netBalance.income(),
                netBalance.expenses().negate(),
                netBalance.income().subtract(netBalance.expenses())
        );
    }
}
