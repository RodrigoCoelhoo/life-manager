package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.*;
import com.rodrigocoelhoo.lifemanager.finances.service.DashboardService;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record MonthOverviewDTO(
    YearMonth yearMonth,
    String totalIncome,
    List<CategorySummaryDTO> incomeCategories,
    String totalExpenses,
    List<CategorySummaryDTO> expenseCategories,
    String netBalance,
    List<WalletResponseDTO> wallets,
    List<TransactionResponseDTO> recentTransactions,
    List<TransferenceResponseDTO> recentTransferences,
    List<AutomaticTransactionSimple> automaticTransactions,
    List<MonthNetBalanceResponseDTO> previousMonthsNetBalance
) {
    public static MonthOverviewDTO fromEntities(
            YearMonth yearMonth,
            Currency currency,
            BigDecimal totalIncome,
            Map<ExpenseCategory, BigDecimal> incomeCategories,
            BigDecimal totalExpenses,
            Map<ExpenseCategory, BigDecimal> expenseCategories,
            BigDecimal netBalance,
            List<WalletModel> wallets,
            List<TransactionModel> recentTransactions,
            List<TransferenceModel> recentTransferences,
            List<AutomaticTransactionModel> automaticTransactions,
            LinkedHashMap<YearMonth, DashboardService.Netbalance> previousMonthsNetBalance
    ) {
        return new MonthOverviewDTO(
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
                currency.format(netBalance),
                wallets.stream().map(WalletResponseDTO::fromEntity).toList(),
                recentTransactions.stream().map(TransactionResponseDTO::fromEntity).toList(),
                recentTransferences.stream().map(TransferenceResponseDTO::fromEntity).toList(),
                automaticTransactions.stream().map(AutomaticTransactionSimple::fromEntity).toList(),
                previousMonthsNetBalance.entrySet().stream()
                        .map(e -> MonthNetBalanceResponseDTO.fromEntities(
                                e.getKey(), e.getValue()
                            )
                        ).toList()
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
