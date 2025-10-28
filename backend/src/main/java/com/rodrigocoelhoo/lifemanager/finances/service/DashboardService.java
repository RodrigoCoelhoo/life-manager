package com.rodrigocoelhoo.lifemanager.finances.service;

import com.rodrigocoelhoo.lifemanager.finances.dto.DashboardOverviewDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseCategory;
import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseType;
import com.rodrigocoelhoo.lifemanager.finances.model.TransactionModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class DashboardService {

    private final TransactionService transactionService;

    public DashboardService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public DashboardOverviewDTO getMonthOverview(YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<TransactionModel> transactions = transactionService.getTransactionsByRange(start, end);

        BigDecimal totalExpenses = BigDecimal.ZERO;
        LinkedHashMap<ExpenseCategory, BigDecimal> expenses = new LinkedHashMap<>();

        BigDecimal totalIncome = BigDecimal.ZERO;
        LinkedHashMap<ExpenseCategory, BigDecimal> income = new LinkedHashMap<>();

        for(TransactionModel transaction : transactions) {
            ExpenseCategory transactionCategory = transaction.getCategory();

            if (transactionCategory.getType().equals(ExpenseType.EXPENSE)) {
                totalExpenses = totalExpenses.add(transaction.getAmount());

                expenses.put(transactionCategory,
                    expenses.getOrDefault(transactionCategory, BigDecimal.ZERO)
                            .add(transaction.getAmount())
                );
            }
            else {
                totalIncome = totalIncome.add(transaction.getAmount());

                income.put(transactionCategory,
                        income.getOrDefault(transactionCategory, BigDecimal.ZERO)
                                .add(transaction.getAmount())
                );
            }
        }

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        return DashboardOverviewDTO.fromEntities(yearMonth, totalIncome, income, totalExpenses, expenses, netBalance);
    }
}
