package com.rodrigocoelhoo.lifemanager.finances.service;

import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.finances.dto.AutomaticTransactionDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.*;
import com.rodrigocoelhoo.lifemanager.finances.repository.AutomaticTransactionRepository;
import com.rodrigocoelhoo.lifemanager.finances.repository.TransactionRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class AutomaticTransactionService {

    private final UserService userService;
    private final AutomaticTransactionRepository automaticTransactionRepository;
    private final WalletService walletService;
    private final TransactionRepository transactionRepository;

    public AutomaticTransactionService(
            UserService userService,
           AutomaticTransactionRepository automaticTransactionRepository,
           WalletService walletService,
           TransactionRepository transactionRepository
    ) {
        this.userService = userService;
        this.automaticTransactionRepository = automaticTransactionRepository;
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
    }

    public AutomaticTransactionModel getAutomaticTransaction(Long id) {
        UserModel user = userService.getLoggedInUser();
        return automaticTransactionRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new BadRequestException("Automatic transaction with ID '" + id + "' doesn't belong to the current user"));
    }

    public Page<AutomaticTransactionModel> getAllAutomaticTransactions(
            Pageable pageable
    ) {
        UserModel user = userService.getLoggedInUser();
        return automaticTransactionRepository.findAllByUser(user, pageable);
    }

    private ExpenseCategory validateCategory(
            String category
    ) {
        try {
            return ExpenseCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Category '" + category.toUpperCase() + "' doesn't exist.\n" +
                    "Valid Categories: " + ExpenseCategory.all());
        }
    }

    private TransactionRecurrence validateRecurrence(
            String recurrence
    ) {
        try {
            return TransactionRecurrence.valueOf(recurrence.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Recurrence '" + recurrence.toUpperCase() + "' doesn't exist.\n" +
                    "Valid recurrence: " + TransactionRecurrence.all());
        }
    }

    @Transactional
    public AutomaticTransactionModel createAutomaticTransaction(AutomaticTransactionDTO data) {
        ExpenseCategory category = validateCategory(data.category());
        TransactionRecurrence recurrence = validateRecurrence(data.recurrence());

        UserModel user = userService.getLoggedInUser();
        WalletModel wallet = walletService.getWallet(data.walletId());
        AutomaticTransactionModel automaticTransaction = AutomaticTransactionModel.builder()
                .user(user)
                .wallet(wallet)
                .name(data.name())
                .amount(data.amount())
                .category(category)
                .type(category.getType())
                .recurrence(recurrence)
                .interval(data.interval())
                .description(data.description())
                .nextTransactionDate(data.nextTransactionDate())
                .build();

        return automaticTransactionRepository.save(automaticTransaction);
    }

    @Transactional
    public AutomaticTransactionModel updateAutomaticTransaction(
            Long id,
            AutomaticTransactionDTO data
    ) {
        ExpenseCategory category = validateCategory(data.category());
        TransactionRecurrence recurrence = validateRecurrence(data.recurrence());

        AutomaticTransactionModel automaticTransaction = getAutomaticTransaction(id);
        WalletModel wallet = walletService.getWallet(data.walletId());

        automaticTransaction.setWallet(wallet);
        automaticTransaction.setName(data.name());
        automaticTransaction.setAmount(data.amount());
        automaticTransaction.setCategory(category);
        automaticTransaction.setType(category.getType());
        automaticTransaction.setRecurrence(recurrence);
        automaticTransaction.setInterval(data.interval());
        automaticTransaction.setDescription(data.description());
        automaticTransaction.setNextTransactionDate(data.nextTransactionDate());

        return automaticTransactionRepository.save(automaticTransaction);
    }

    @Transactional
    public void deleteAutomaticTransaction(Long id) {
        AutomaticTransactionModel automaticTransaction = getAutomaticTransaction(id);
        automaticTransactionRepository.delete(automaticTransaction);
    }

    public void processDailyAutomaticTransactions() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<AutomaticTransactionModel> dueTransactions = automaticTransactionRepository.findByNextTransactionDate(today);

        for (AutomaticTransactionModel autoTx : dueTransactions) {
            try {
                TransactionModel transaction = TransactionModel.builder()
                        .user(autoTx.getUser())
                        .wallet(autoTx.getWallet())
                        .amount(autoTx.getAmount())
                        .category(autoTx.getCategory())
                        .type(autoTx.getType())
                        .description(autoTx.getDescription())
                        .date(today)
                        .build();

                transactionRepository.save(transaction);

                LocalDate nextDate = calculateNextDate(
                        autoTx.getNextTransactionDate(),
                        autoTx.getRecurrence(),
                        autoTx.getInterval()
                );
                autoTx.setNextTransactionDate(nextDate);
                automaticTransactionRepository.save(autoTx);

            } catch (Exception e) {
                System.err.println("Failed to process automatic transaction " + autoTx.getId() + ": " + e.getMessage());
            }
        }
    }


    private LocalDate calculateNextDate(LocalDate currentDate, TransactionRecurrence recurrence, int interval) {
        return switch (recurrence) {
            case DAILY -> currentDate.plusDays(interval);
            case WEEKLY -> currentDate.plusWeeks(interval);
            case MONTHLY -> currentDate.plusMonths(interval);
            case YEARLY -> currentDate.plusYears(interval);
            default -> throw new IllegalArgumentException("Unknown recurrence: " + recurrence);
        };
    }
}
