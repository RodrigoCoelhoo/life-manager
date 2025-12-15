package com.rodrigocoelhoo.lifemanager.finances.service;

import com.rodrigocoelhoo.lifemanager.finances.dto.MonthOverviewDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.*;
import com.rodrigocoelhoo.lifemanager.finances.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class DashboardService {

    private final TransactionService transactionService;
    private final WalletService walletService;
    private final TransferenceService transferenceService;

    public DashboardService(
            TransactionService transactionService,
            WalletService walletService,
            TransferenceService transferenceService
    ) {
        this.transactionService = transactionService;
        this.walletService = walletService;
        this.transferenceService = transferenceService;
    }

    public MonthOverviewDTO getMonthOverview(YearMonth yearMonth, Currency currency) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end   = yearMonth.atEndOfMonth();

        List<TransactionModel> currentMonthTransactions = transactionService.getTransactionsByRange(start, end);

        BigDecimal totalExpenses = BigDecimal.ZERO;
        LinkedHashMap<ExpenseCategory, BigDecimal> expenses = new LinkedHashMap<>();

        BigDecimal totalIncome = BigDecimal.ZERO;
        LinkedHashMap<ExpenseCategory, BigDecimal> income = new LinkedHashMap<>();

        for(TransactionModel transaction : currentMonthTransactions) {
            ExpenseCategory transactionCategory = transaction.getCategory();
            BigDecimal amount = transaction.getCurrency().convertTo(transaction.getAmount(), currency);

            if (transactionCategory.getType().equals(ExpenseType.EXPENSE)) {
                totalExpenses = totalExpenses.add(amount);

                expenses.put(transactionCategory,
                    expenses.getOrDefault(transactionCategory, BigDecimal.ZERO)
                            .add(amount)
                );
            }
            else {
                totalIncome = totalIncome.add(amount);

                income.put(transactionCategory,
                        income.getOrDefault(transactionCategory, BigDecimal.ZERO)
                                .add(amount)
                );
            }
        }

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);
        List<TransactionModel> recentTransactions = currentMonthTransactions.subList(0, Math.min(currentMonthTransactions.size(), 5));
        List<TransferenceModel> recentTransferences = transferenceService.get5RecentTransferences(start, end);

        boolean isCurrentMonth = YearMonth.now().equals(yearMonth);
        List<WalletModel> wallets = new ArrayList<>();
        List<AutomaticTransactionModel> automaticTransactions = new ArrayList<>();
        LinkedHashMap<YearMonth, Netbalance> previousMonthsNetBalance = new LinkedHashMap<>();

        if(isCurrentMonth) {
            Pageable pageable = PageRequest.of(0, 5, Sort.by("balance").descending());
            wallets = walletService.getWallets(pageable, "").stream().toList();

            // Automatic transactions

            // NetBalance
            List<TransactionModel> pastMonthTransactions = transactionService.getTransactionsByRange(
                    yearMonth.minusMonths(5).atDay(1),
                    yearMonth.minusMonths(1).atEndOfMonth()
            );

            LinkedHashMap<YearMonth, List<TransactionModel>> months = new LinkedHashMap<>();
            for (int i = 1; i <= 5; i++) {
                months.put(yearMonth.minusMonths(i), new ArrayList<>());
            }

            for (TransactionModel tx : pastMonthTransactions) {
                YearMonth txMonth = YearMonth.from(tx.getDate());

                if (months.containsKey(txMonth)) {
                    months.get(txMonth).add(tx);
                }
            }

            for (Map.Entry<YearMonth, List<TransactionModel>> entry : months.entrySet()) {
                Netbalance monthNetbalance = calculateNetBalanceForList(entry.getValue(), currency);
                previousMonthsNetBalance.put(entry.getKey(), monthNetbalance);
            }
            previousMonthsNetBalance.put(yearMonth, new Netbalance(totalIncome, totalExpenses));
        }

        return MonthOverviewDTO.fromEntities(
                yearMonth,
                currency,
                totalIncome,
                income,
                totalExpenses,
                expenses,
                netBalance,
                wallets,
                recentTransactions,
                recentTransferences,
                automaticTransactions,
                previousMonthsNetBalance
        );
    }

    private Netbalance calculateNetBalanceForList(
            List<TransactionModel> txs,
            Currency targetCurrency
    ) {
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;

        for (TransactionModel tx : txs) {
            BigDecimal converted = tx.getWallet().getCurrency().convertTo(tx.getAmount(), targetCurrency);

            if (tx.getCategory().getType() == ExpenseType.EXPENSE) {
                expenses = expenses.add(converted);
            } else {
                income = income.add(converted);
            }
        }

        return new Netbalance(income, expenses);
    }

    @Getter
    @AllArgsConstructor
    public class Netbalance {
        private final BigDecimal income;
        private final BigDecimal expenses;
    }
}
