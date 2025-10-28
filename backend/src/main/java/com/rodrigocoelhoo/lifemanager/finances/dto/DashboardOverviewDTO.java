package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseCategory;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public record DashboardOverviewDTO(
    YearMonth yearMonth,
    BigDecimal totalIncome,
    List<CategorySummaryDTO> incomeCategories,
    BigDecimal totalExpenses,
    List<CategorySummaryDTO> expenseCategories,
    BigDecimal netBalance
) {
    public static DashboardOverviewDTO fromEntities(
            YearMonth yearMonth,
            BigDecimal totalIncome,
            Map<ExpenseCategory, BigDecimal> incomeCategories,
            BigDecimal totalExpenses,
            Map<ExpenseCategory, BigDecimal> expenseCategories,
            BigDecimal netBalance
    ) {
        return new DashboardOverviewDTO(
                yearMonth,
                totalIncome,
                incomeCategories.entrySet().stream()
                        .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                        .map(CategorySummaryDTO::fromEntity)
                        .toList(),
                totalExpenses,
                expenseCategories.entrySet().stream()
                        .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                        .map(CategorySummaryDTO::fromEntity)
                        .toList(),
                netBalance
        );
    }


    public record CategorySummaryDTO(
            String category,
            BigDecimal amount
    ) {
        public static CategorySummaryDTO fromEntity(Map.Entry<ExpenseCategory, BigDecimal> model) {
            return new CategorySummaryDTO(
                    model.getKey().toString(),
                    model.getValue()
            );
        }
    }
}
