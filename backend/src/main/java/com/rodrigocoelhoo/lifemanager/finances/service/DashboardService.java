package com.rodrigocoelhoo.lifemanager.finances.service;

import com.rodrigocoelhoo.lifemanager.finances.dto.*;
import com.rodrigocoelhoo.lifemanager.finances.model.*;
import com.rodrigocoelhoo.lifemanager.finances.model.Currency;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class DashboardService {

    private final TransactionService transactionService;
    private final WalletService walletService;
    private final TransferenceService transferenceService;
    private final AutomaticTransactionService automaticTransactionService;

    private static final String CACHE_DASHBOARD = "financesDashboard";

    public DashboardService(
            TransactionService transactionService,
            WalletService walletService,
            TransferenceService transferenceService,
            AutomaticTransactionService automaticTransactionService
    ) {
        this.transactionService = transactionService;
        this.walletService = walletService;
        this.transferenceService = transferenceService;
        this.automaticTransactionService = automaticTransactionService;
    }

    @Cacheable(
            value = CACHE_DASHBOARD,
            key = "T(com.rodrigocoelhoo.lifemanager.config.RedisCacheService).getCurrentUsername() + " +
                    "'::yearMonth:' + #yearMonth + " +
                    "'::currency:' + #currency.name()"
    )
    public MonthOverviewDTO getMonthOverview(YearMonth yearMonth, Currency currency) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end   = yearMonth.atEndOfMonth();

        List<TransactionInternalDTO> currentMonthTransactions = transactionService.getTransactionsByRange(start, end);

        BigDecimal totalExpenses = BigDecimal.ZERO;
        LinkedHashMap<ExpenseCategory, BigDecimal> expenses = new LinkedHashMap<>();

        BigDecimal totalIncome = BigDecimal.ZERO;
        LinkedHashMap<ExpenseCategory, BigDecimal> income = new LinkedHashMap<>();

        for(TransactionInternalDTO transaction : currentMonthTransactions) {
            ExpenseCategory transactionCategory = transaction.category();
            BigDecimal amount = transaction.currency().convertTo(transaction.amount(), currency);

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
        List<TransactionInternalDTO> recentTransactions = currentMonthTransactions.subList(0, Math.min(currentMonthTransactions.size(), 5));
        List<TransferenceResponseDTO> recentTransferences = transferenceService.get5RecentTransferences(start, end);

        boolean isCurrentMonth = YearMonth.now().equals(yearMonth);
        List<WalletResponseDTO> wallets = new ArrayList<>();
        List<AutomaticTransactionSimple> automaticTransactions = new ArrayList<>();
        LinkedHashMap<YearMonth, Netbalance> previousMonthsNetBalance = new LinkedHashMap<>();

        if(isCurrentMonth) {
            Pageable pageable = PageRequest.of(0, 5, Sort.by("balance").descending());
            wallets = walletService.getWallets(pageable, "").stream().toList();

            automaticTransactions = automaticTransactionService.get5NextAutomaticTransaction();

            // NetBalance
            List<TransactionInternalDTO> pastMonthTransactions = transactionService.getTransactionsByRange(
                    yearMonth.minusMonths(5).atDay(1),
                    yearMonth.minusMonths(1).atEndOfMonth()
            );

            LinkedHashMap<YearMonth, List<TransactionInternalDTO>> months = new LinkedHashMap<>();
            for (int i = 1; i <= 5; i++) {
                months.put(yearMonth.minusMonths(i), new ArrayList<>());
            }

            for (TransactionInternalDTO tx : pastMonthTransactions) {
                YearMonth txMonth = YearMonth.from(tx.date());

                if (months.containsKey(txMonth)) {
                    months.get(txMonth).add(tx);
                }
            }

            for (Map.Entry<YearMonth, List<TransactionInternalDTO>> entry : months.entrySet()) {
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
            List<TransactionInternalDTO> txs,
            Currency targetCurrency
    ) {
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;

        for (TransactionInternalDTO tx : txs) {
            BigDecimal converted = tx.wallet().currency().convertTo(tx.amount(), targetCurrency);

            if (tx.category().getType() == ExpenseType.EXPENSE) {
                expenses = expenses.add(converted);
            } else {
                income = income.add(converted);
            }
        }

        return new Netbalance(income, expenses);
    }

    public record Netbalance(BigDecimal income, BigDecimal expenses) implements Serializable { }
}
