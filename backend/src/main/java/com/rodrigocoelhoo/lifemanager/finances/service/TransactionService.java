package com.rodrigocoelhoo.lifemanager.finances.service;

import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.finances.dto.TransactionDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseCategory;
import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseType;
import com.rodrigocoelhoo.lifemanager.finances.model.TransactionModel;
import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;
import com.rodrigocoelhoo.lifemanager.finances.repository.TransactionRepository;
import com.rodrigocoelhoo.lifemanager.finances.repository.WalletRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final WalletRepository walletRepository;

    public TransactionService(
            UserService userService,
            TransactionRepository transactionRepository,
            WalletService walletService,
            WalletRepository walletRepository
    ) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
        this.walletRepository = walletRepository;
    }

    public Page<TransactionModel> getAllTransactions(
            Pageable pageable
    ) {
        UserModel user = userService.getLoggedInUser();
        return transactionRepository.findAllByUser(user, pageable);
    }

    public List<TransactionModel> getTransactionsByRange(
            LocalDate start,
            LocalDate end
    ) {
        UserModel user = userService.getLoggedInUser();
        return transactionRepository.findAllByUserAndDateBetween(user, start, end);
    }

    public TransactionModel getTransaction(
            Long id
    ) {
        UserModel user = userService.getLoggedInUser();
        return transactionRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new ResourceNotFound(
                        "Transaction with ID '" + id + "' doesn't belong to the current user")
                );
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

    private void adjustWalletBalance(
            WalletModel wallet,
            BigDecimal oldAmount,
            ExpenseType oldType,
            BigDecimal newAmount,
            ExpenseType newType
    ) {
        if(oldType != null && oldType.equals(newType) && oldAmount.compareTo(newAmount) == 0) return;

        BigDecimal currentAmount = wallet.getBalance();

        BigDecimal revertedAmount;
        if(oldType != null) {
            revertedAmount = oldType.equals(ExpenseType.EXPENSE) ?
                    currentAmount.add(oldAmount):
                    currentAmount.subtract(oldAmount);
        } else {
            revertedAmount = currentAmount;
        }

        BigDecimal balance;
        if(newType != null) {
            balance = newType.equals(ExpenseType.EXPENSE) ?
                    revertedAmount.subtract(newAmount):
                    revertedAmount.add(newAmount);
        } else {
            balance = revertedAmount;
        }

        if(balance.compareTo(BigDecimal.ZERO) < 0)
            throw new BadRequestException("Wallet ID '" + wallet.getId() + "' doesn't have enough balance to complete the transaction.");

        wallet.setBalance(balance);
        walletRepository.save(wallet);
    }

    @Transactional
    public TransactionModel createTransaction(
            @Valid TransactionDTO data
    ) {
        ExpenseCategory category = validateCategory(data.category());
        WalletModel wallet = walletService.getWallet(data.walletId());

        adjustWalletBalance(wallet, BigDecimal.ZERO, null, data.amount(), category.getType());

        TransactionModel transaction = TransactionModel.builder()
                .user(userService.getLoggedInUser())
                .wallet(wallet)
                .amount(data.amount())
                .type(category.getType())
                .description(data.description())
                .date(data.date())
                .category(category)
                .build();

        return transactionRepository.save(transaction);
    }

    @Transactional
    public TransactionModel updateTransaction(
            Long id,
            @Valid TransactionDTO data
    ) {
        ExpenseCategory newCategory = validateCategory(data.category());
        WalletModel wallet = walletService.getWallet(data.walletId());
        TransactionModel transaction = getTransaction(id);

        adjustWalletBalance(wallet, transaction.getAmount(), transaction.getType(), data.amount(), newCategory.getType());

        transaction.setAmount(data.amount());
        transaction.setDate(data.date());
        transaction.setDescription(data.description());
        transaction.setCategory(newCategory);
        transaction.setType(newCategory.getType());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        TransactionModel transaction = getTransaction(id);
        WalletModel wallet = transaction.getWallet();

        adjustWalletBalance(wallet, transaction.getAmount(), transaction.getType(), BigDecimal.ZERO, null);

        transactionRepository.delete(transaction);
    }
}
