package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.Currency;
import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseCategory;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public record DashboardOverviewDTO(
    YearMonth yearMonth,
    String totalIncome,
    List<CategorySummaryDTO> incomeCategories,
    String totalExpenses,
    List<CategorySummaryDTO> expenseCategories,
    String netBalance
) {
    public static DashboardOverviewDTO fromEntities(
            YearMonth yearMonth,
            Currency currency,
            BigDecimal totalIncome,
            Map<ExpenseCategory, BigDecimal> incomeCategories,
            BigDecimal totalExpenses,
            Map<ExpenseCategory, BigDecimal> expenseCategories,
            BigDecimal netBalance
    ) {
        return new DashboardOverviewDTO(
                yearMonth,
                currency.format(totalIncome),
                incomeCategories.entrySet().stream()
                        .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                        .map(entry -> CategorySummaryDTO.fromEntity(entry, currency))
                        .toList(),
                currency.format(totalExpenses),
                expenseCategories.entrySet().stream()
                        .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                        .map(entry -> CategorySummaryDTO.fromEntity(entry, currency))
                        .toList(),
                currency.format(netBalance)
        );
    }


    public record CategorySummaryDTO(
            String category,
            String amount
    ) {
        public static CategorySummaryDTO fromEntity(Map.Entry<ExpenseCategory, BigDecimal> model, Currency currency) {
            return new CategorySummaryDTO(
                    model.getKey().toString(),
                    currency.format(model.getValue())
            );
        }
    }
}
