package com.rodrigocoelhoo.lifemanager.finances.service;

import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.finances.dto.TransactionDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.*;
import com.rodrigocoelhoo.lifemanager.finances.repository.TransactionRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserModel();
        user.setId(1L);
        user.setUsername("testuser");
        when(userService.getLoggedInUser()).thenReturn(user);
    }

    @Nested
    @DisplayName("getAllTransactions")
    class GetAllTransactionsTests {

        @Test
        @DisplayName("should return all transactions for logged-in user")
        void shouldReturnAllTransactions() {
            TransactionModel t1 = new TransactionModel();
            TransactionModel t2 = new TransactionModel();

            PageImpl<TransactionModel> page = new PageImpl<>(List.of(t1, t2));
            when(transactionRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            Page<TransactionModel> result = transactionService.getAllTransactions(Pageable.unpaged());

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).containsExactlyInAnyOrder(t1, t2);
            verify(transactionRepository).findAllByUser(user, Pageable.unpaged());
        }
    }

    @Nested
    @DisplayName("getTransactionsByRange")
    class GetTransactionsByRangeTests {

        @Test
        @DisplayName("should return transactions within date range")
        void shouldReturnTransactionsByRange() {
            TransactionModel t1 = new TransactionModel();
            TransactionModel t2 = new TransactionModel();
            TransactionModel t3 = new TransactionModel();

            t1.setDate(LocalDate.of(2026, 1, 1));
            t2.setDate(LocalDate.of(2026, 1, 2));
            t3.setDate(LocalDate.of(2026, 1, 3));

            when(transactionRepository.findAllByUserAndDateBetweenOrderByDateDescIdDesc(
                    user,
                    LocalDate.of(2026, 1, 2),
                    LocalDate.of(2026, 1, 3)
            )).thenReturn(List.of(t2, t3));

            List<TransactionModel> result = transactionService.getTransactionsByRange(
                    LocalDate.of(2026, 1, 2),
                    LocalDate.of(2026, 1, 3)
            );

            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder(t2, t3);
            verify(transactionRepository).findAllByUserAndDateBetweenOrderByDateDescIdDesc(
                    user,
                    LocalDate.of(2026, 1, 2),
                    LocalDate.of(2026, 1, 3)
            );
        }
    }

    @Nested
    @DisplayName("getTransaction")
    class GetTransactionTests {

        @Test
        @DisplayName("should return transaction when it belongs to user")
        void shouldReturnTransaction() {
            TransactionModel t1 = new TransactionModel();
            t1.setId(1L);

            when(transactionRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(t1));
            TransactionModel result = transactionService.getTransaction(1L);

            assertThat(result.getId()).isEqualTo(1L);
            verify(transactionRepository).findByUserAndId(user, 1L);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if transaction does not belong to user")
        void shouldThrowIfTransactionNotFound() {
            when(transactionRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> transactionService.getTransaction(1L));

            assertThat(exception.getMessage()).contains("Transaction with ID '1' doesn't belong to the current user");
        }
    }

    @Nested
    @DisplayName("createTransaction")
    class CreateTransactionTests {

        @Test
        @DisplayName("should create transaction successfully")
        void shouldCreateTransactionSuccessfully() {
            TransactionDTO transactionDTO = new TransactionDTO(
                    1L,
                    new BigDecimal(10),
                    "",
                    LocalDate.now(),
                    "FOOD"
            );

            WalletModel wallet = new WalletModel();
            wallet.setBalance(new BigDecimal(100));
            wallet.setCurrency(Currency.AUD);

            when(walletService.getWallet(transactionDTO.walletId())).thenReturn(wallet);
            when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            TransactionModel result = transactionService.createTransaction(transactionDTO);

            assertThat(result.getWallet().getBalance()).isEqualTo(new BigDecimal(90));
            assertThat(result.getCategory()).isEqualTo(ExpenseCategory.FOOD);
            assertThat(result.getType()).isEqualTo(ExpenseType.EXPENSE);
            assertThat(result.getAmount()).isEqualTo(transactionDTO.amount());
            assertThat(result.getCurrency()).isEqualTo(Currency.AUD);

            verify(transactionRepository).save(any(TransactionModel.class));
        }

        @Test
        @DisplayName("should throw BadRequestException for invalid category")
        void shouldThrowForInvalidCategory() {
            TransactionDTO transactionDTO = new TransactionDTO(
                    1L,
                    new BigDecimal(10),
                    "",
                    LocalDate.now(),
                    "fuud"
            );
            BadRequestException exception = assertThrows(BadRequestException.class, () -> transactionService.createTransaction(transactionDTO));

            assertThat(exception.getMessage()).contains("Category 'FUUD' doesn't exist.\nValid Categories: " + ExpenseCategory.all());
            verify(transactionRepository, never()).save(any(TransactionModel.class));
        }

        @Test
        @DisplayName("should throw BadRequestException if wallet has insufficient balance")
        void shouldThrowIfInsufficientBalance() {
            TransactionDTO transactionDTO = new TransactionDTO(
                    1L,
                    new BigDecimal(10),
                    "",
                    LocalDate.now(),
                    "FOOD"
            );

            WalletModel wallet = new WalletModel();
            wallet.setId(1L);
            wallet.setBalance(new BigDecimal(9));

            when(walletService.getWallet(transactionDTO.walletId())).thenReturn(wallet);
            BadRequestException exception = assertThrows(BadRequestException.class, () -> transactionService.createTransaction(transactionDTO));

            assertThat(exception.getMessage()).contains("Wallet ID '1' doesn't have enough balance.");
            verify(transactionRepository, never()).save(any(TransactionModel.class));
        }
    }

    @Nested
    @DisplayName("updateTransaction")
    class UpdateTransactionTests {

        @Test
        @DisplayName("should update transaction successfully")
        void shouldUpdateTransactionSuccessfully() {
            TransactionDTO transactionDTO = new TransactionDTO(
                    2L,
                    new BigDecimal(10),
                    "",
                    LocalDate.now(),
                    "FOOD"
            );

            WalletModel existingWallet = new WalletModel();
            existingWallet.setId(1L);
            existingWallet.setBalance(new BigDecimal(100));
            existingWallet.setCurrency(Currency.AUD);

            TransactionModel existing = TransactionModel.builder()
                    .amount(new BigDecimal(100))
                    .category(ExpenseCategory.SALARY)
                    .wallet(existingWallet)
                    .build();

            WalletModel newWallet = new WalletModel();
            newWallet.setId(2L);
            newWallet.setBalance(new BigDecimal(20));
            newWallet.setCurrency(Currency.EUR);

            when(walletService.getWallet(transactionDTO.walletId())).thenReturn(newWallet);
            when(transactionRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(existing));
            when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            TransactionModel result = transactionService.updateTransaction(1L, transactionDTO);

            assertThat(result.getWallet().getBalance()).isEqualTo(new BigDecimal(10));
            assertThat(result.getCategory()).isEqualTo(ExpenseCategory.FOOD);
            assertThat(result.getType()).isEqualTo(ExpenseType.EXPENSE);
            assertThat(result.getAmount()).isEqualTo(transactionDTO.amount());
            assertThat(result.getCurrency()).isEqualTo(Currency.EUR);

            assertThat(existingWallet.getBalance()).isEqualTo(BigDecimal.ZERO);
            verify(transactionRepository).save(any(TransactionModel.class));
        }

        @Test
        @DisplayName("should throw BadRequestException for invalid category on update")
        void shouldThrowForInvalidCategoryOnUpdate() {
            TransactionDTO transactionDTO = new TransactionDTO(
                    1L,
                    new BigDecimal(10),
                    "",
                    LocalDate.now(),
                    "fuud"
            );
            BadRequestException exception = assertThrows(BadRequestException.class, () -> transactionService.updateTransaction(1L, transactionDTO));

            assertThat(exception.getMessage()).contains("Category 'FUUD' doesn't exist.\nValid Categories: " + ExpenseCategory.all());
            verify(transactionRepository, never()).save(any(TransactionModel.class));
        }

        @Test
        @DisplayName("should throw BadRequestException if wallet balance becomes negative")
        void shouldThrowIfBalanceNegativeOnUpdate() {
            TransactionDTO transactionDTO = new TransactionDTO(
                    2L,
                    new BigDecimal(10),
                    "",
                    LocalDate.now(),
                    "FOOD"
            );

            WalletModel existingWallet = new WalletModel();
            existingWallet.setId(1L);
            existingWallet.setBalance(new BigDecimal(90));
            existingWallet.setCurrency(Currency.AUD);

            TransactionModel existing = TransactionModel.builder()
                    .amount(new BigDecimal(100))
                    .category(ExpenseCategory.SALARY)
                    .wallet(existingWallet)
                    .type(ExpenseType.INCOME)
                    .build();

            WalletModel newWallet = new WalletModel();
            newWallet.setId(2L);
            newWallet.setBalance(new BigDecimal(20));
            newWallet.setCurrency(Currency.EUR);

            when(walletService.getWallet(transactionDTO.walletId())).thenReturn(newWallet);
            when(transactionRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(existing));

            BadRequestException exception = assertThrows(BadRequestException.class, () -> transactionService.updateTransaction(1L, transactionDTO));

            assertThat(exception.getMessage()).contains("Wallet ID '1' doesn't have enough balance.");
            verify(transactionRepository, never()).save(any(TransactionModel.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFound if transaction does not exist")
        void shouldThrowIfTransactionDoesNotExist() {
            TransactionDTO transactionDTO = new TransactionDTO(
                    1L,
                    new BigDecimal(10),
                    "",
                    LocalDate.now(),
                    "FOOD"
            );

            WalletModel wallet = new WalletModel();

            when(walletService.getWallet(1L)).thenReturn(wallet);
            when(transactionRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> transactionService.updateTransaction(1L, transactionDTO));

            assertThat(exception.getMessage()).contains("Transaction with ID '1' doesn't belong to the current user");
        }
    }

    @Nested
    @DisplayName("deleteTransaction")
    class DeleteTransactionTests {

        @Test
        @DisplayName("should delete transaction successfully")
        void shouldDeleteTransactionSuccessfully() {
            WalletModel wallet = new WalletModel();
            wallet.setBalance(new BigDecimal(15));

            TransactionModel t1 = new TransactionModel();
            t1.setId(1L);
            t1.setType(ExpenseType.EXPENSE);
            t1.setWallet(wallet);
            t1.setAmount(new BigDecimal(10));

            when(walletService.getWallet(1L)).thenReturn(wallet);
            when(transactionRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(t1));

            transactionService.deleteTransaction(1L);

            assertThat(wallet.getBalance()).isEqualTo(new BigDecimal(25));
            verify(transactionRepository).delete(any(TransactionModel.class));
        }

        @Test
        @DisplayName("should throw BadRequestException if deletion causes negative balance")
        void shouldThrowIfNegativeBalanceOnDelete() {
            WalletModel wallet = new WalletModel();
            wallet.setBalance(new BigDecimal(15));

            TransactionModel t1 = new TransactionModel();
            t1.setId(1L);
            t1.setType(ExpenseType.INCOME);
            t1.setWallet(wallet);
            t1.setAmount(new BigDecimal(20));

            when(transactionRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(t1));

            BadRequestException exception = assertThrows(BadRequestException.class, () -> transactionService.deleteTransaction(1L));

            assertThat(exception.getMessage()).isEqualTo("Cannot delete this transaction because it produces a negative balance.");
            verify(transactionRepository, never()).delete(any(TransactionModel.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFound if transaction does not exist")
        void shouldThrowIfTransactionDoesNotExist() {
            when(transactionRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> transactionService.deleteTransaction(1L));

            assertThat(exception.getMessage()).isEqualTo("Transaction with ID '1' doesn't belong to the current user");
            verify(transactionRepository, never()).delete(any(TransactionModel.class));
        }
    }
}
