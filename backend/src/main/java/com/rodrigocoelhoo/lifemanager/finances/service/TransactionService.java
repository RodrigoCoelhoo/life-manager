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

    public TransactionService(
            UserService userService,
            TransactionRepository transactionRepository,
            WalletService walletService
    ) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
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
        return transactionRepository.findAllByUserAndDateBetweenOrderByDateDescIdDesc(user, start, end);
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

    @Transactional
    public TransactionModel createTransaction(
            @Valid TransactionDTO data
    ) {
        ExpenseCategory category = validateCategory(data.category());
        WalletModel wallet = walletService.getWallet(data.walletId());

        BigDecimal newBalance = wallet.getBalance().add(
                ExpenseType.normalize(category.getType(), data.amount())
        );
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Wallet ID '" + wallet.getId() + "' doesn't have enough balance.");
        }
        wallet.setBalance(newBalance);

        TransactionModel transaction = TransactionModel.builder()
                .user(userService.getLoggedInUser())
                .wallet(wallet)
                .amount(data.amount())
                .type(category.getType())
                .description(data.description())
                .date(data.date())
                .category(category)
                .currency(wallet.getCurrency())
                .build();

        return transactionRepository.save(transaction);
    }

    @Transactional
    public TransactionModel updateTransaction(
            Long id,
            @Valid TransactionDTO data
    ) {
        ExpenseCategory newCategory = validateCategory(data.category());

        WalletModel newWallet = walletService.getWallet(data.walletId());
        TransactionModel transaction = getTransaction(id);
        WalletModel currentWallet = transaction.getWallet();

        adjustWalletBalance(currentWallet, newWallet, newCategory.getType(), data.amount(), transaction);

        transaction.setWallet(newWallet);
        transaction.setAmount(data.amount());
        transaction.setDate(data.date());
        transaction.setDescription(data.description());
        transaction.setCategory(newCategory);
        transaction.setType(newCategory.getType());
        transaction.setCurrency(newWallet.getCurrency());

        return transactionRepository.save(transaction);
    }

    private void adjustWalletBalance(
            WalletModel currentWallet,
            WalletModel newWallet,
            ExpenseType newType,
            BigDecimal newAmount,
            TransactionModel transaction
    ) {
        if (currentWallet.getId().equals(newWallet.getId()) &&
                transaction.getType().equals(newType) &&
                transaction.getAmount().compareTo(newAmount) == 0) {
            return;
        }

        BigDecimal currentAmount = transaction.getAmount();
        ExpenseType currentType = transaction.getType();
        BigDecimal oldDelta = ExpenseType.normalize(currentType, currentAmount);
        BigDecimal newDelta = ExpenseType.normalize(newType, newAmount);

        BigDecimal oldBalance = currentWallet.getBalance().subtract(oldDelta);

        // SAME WALLET
        if (currentWallet.getId().equals(newWallet.getId())) {
            BigDecimal updated = oldBalance.add(newDelta);

            if (updated.compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException("Wallet ID '" + currentWallet.getId() + "' doesn't have enough balance.");
            }

            currentWallet.setBalance(updated);
            return;
        }

        // DIFFERENT WALLETS
        if (oldBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Wallet ID '" + currentWallet.getId() + "' doesn't have enough balance.");
        }
        currentWallet.setBalance(oldBalance);

        BigDecimal newBalance = newWallet.getBalance().add(newDelta);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Wallet ID '" + newWallet.getId() + "' doesn't have enough balance.");
        }
        newWallet.setBalance(newBalance);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        TransactionModel transaction = getTransaction(id);
        WalletModel wallet = transaction.getWallet();

        BigDecimal currentAmount = transaction.getAmount();
        ExpenseType currentType = transaction.getType();
        BigDecimal delta = ExpenseType.normalize(currentType, currentAmount);

        BigDecimal newBalance = wallet.getBalance().subtract(delta);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Cannot delete this transaction because it produces a negative balance.");
        }
        wallet.setBalance(newBalance);

        transactionRepository.delete(transaction);
    }
}
