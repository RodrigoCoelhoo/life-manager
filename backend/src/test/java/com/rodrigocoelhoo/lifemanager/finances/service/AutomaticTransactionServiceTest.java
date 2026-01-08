package com.rodrigocoelhoo.lifemanager.finances.service;

import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.finances.dto.AutomaticTransactionDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.*;
import com.rodrigocoelhoo.lifemanager.finances.repository.AutomaticTransactionRepository;
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

class AutomaticTransactionServiceTest {

    @InjectMocks
    private AutomaticTransactionService automaticTransactionService;

    @Mock
    private UserService userService;

    @Mock
    private AutomaticTransactionRepository automaticTransactionRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private TransactionRepository transactionRepository;

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
    @DisplayName("getAutomaticTransaction")
    class GetAutomaticTransactionTests {

        @Test
        @DisplayName("should return automatic transaction when it belongs to user")
        void shouldReturnAutomaticTransaction() {
            AutomaticTransactionModel autoTx = new AutomaticTransactionModel();
            autoTx.setId(1L);

            when(automaticTransactionRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(autoTx));

            AutomaticTransactionModel result = automaticTransactionService.getAutomaticTransaction(1L);

            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should throw BadRequestException if automatic transaction does not belong to user")
        void shouldThrowIfNotFound() {
            when(automaticTransactionRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> automaticTransactionService.getAutomaticTransaction(1L)
            );

            assertThat(exception.getMessage())
                    .contains("Automatic transaction with ID '1' doesn't belong to the current user");
        }
    }

    @Nested
    @DisplayName("getAllAutomaticTransactions")
    class GetAllAutomaticTransactionsTests {

        @Test
        @DisplayName("should return all automatic transactions for logged-in user")
        void shouldReturnAllAutomaticTransactions() {
            Page<AutomaticTransactionModel> page = new PageImpl<>(List.of(new AutomaticTransactionModel(), new AutomaticTransactionModel()));

            when(automaticTransactionRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            Page<AutomaticTransactionModel> result = automaticTransactionService.getAllAutomaticTransactions(Pageable.unpaged());

            assertThat(result.getContent()).hasSize(2);
            verify(automaticTransactionRepository).findAllByUser(user, Pageable.unpaged());
        }
    }

    @Nested
    @DisplayName("get5NextAutomaticTransaction")
    class Get5NextAutomaticTransactionTests {

        @Test
        @DisplayName("should return next 5 automatic transactions")
        void shouldReturnNextAutomaticTransactions() {
            List<AutomaticTransactionModel> list = List.of(new AutomaticTransactionModel(), new AutomaticTransactionModel());

            when(automaticTransactionRepository
                    .findTop5ByUserOrderByNextTransactionDateAscIdDesc(user))
                    .thenReturn(list);

            List<AutomaticTransactionModel> result = automaticTransactionService.get5NextAutomaticTransaction();

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("createAutomaticTransaction")
    class CreateAutomaticTransactionTests {

        @Test
        @DisplayName("should create automatic transaction successfully")
        void shouldCreateAutomaticTransactionSuccessfully() {
            WalletModel wallet = new WalletModel();
            wallet.setCurrency(Currency.EUR);

            AutomaticTransactionDTO dto = new AutomaticTransactionDTO(
                    1L,
                    "Netflix",
                    new BigDecimal("15.99"),
                    "ENTERTAINMENT",
                    "MONTHLY",
                    (short) 1,
                    "",
                    LocalDate.now()
            );

            when(walletService.getWallet(1L)).thenReturn(wallet);
            when(automaticTransactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            AutomaticTransactionModel result = automaticTransactionService.createAutomaticTransaction(dto);

            assertThat(result.getName()).isEqualTo("Netflix");
            assertThat(result.getCategory()).isEqualTo(ExpenseCategory.ENTERTAINMENT);
            assertThat(result.getRecurrence()).isEqualTo(TransactionRecurrence.MONTHLY);
            assertThat(result.getType()).isEqualTo(ExpenseType.EXPENSE);
        }

        @Test
        @DisplayName("should throw BadRequestException for invalid category")
        void shouldThrowForInvalidCategory() {
            AutomaticTransactionDTO dto = new AutomaticTransactionDTO(
                    1L,
                    "Netflix",
                    BigDecimal.TEN,
                    "invalid",
                    "MONTHLY",
                    (short) 1,
                    "",
                    LocalDate.now()
            );

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> automaticTransactionService.createAutomaticTransaction(dto)
            );

            assertThat(exception.getMessage()).contains("Category 'INVALID' doesn't exist");
        }

        @Test
        @DisplayName("should throw BadRequestException for invalid recurrence")
        void shouldThrowForInvalidRecurrence() {
            AutomaticTransactionDTO dto = new AutomaticTransactionDTO(
                    1L,
                    "Netflix",
                    BigDecimal.TEN,
                    "FOOD",
                    "invalid",
                    (short) 1,
                    "",
                    LocalDate.now()
            );

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> automaticTransactionService.createAutomaticTransaction(dto)
            );

            assertThat(exception.getMessage()).contains("Recurrence 'INVALID' doesn't exist");
        }
    }

    @Nested
    @DisplayName("updateAutomaticTransaction")
    class UpdateAutomaticTransactionTests {

        @Test
        @DisplayName("should update automatic transaction successfully")
        void shouldUpdateAutomaticTransactionSuccessfully() {
            WalletModel wallet = new WalletModel();
            wallet.setCurrency(Currency.EUR);

            AutomaticTransactionModel existing = new AutomaticTransactionModel();
            existing.setId(1L);

            AutomaticTransactionDTO dto = new AutomaticTransactionDTO(
                    1L,
                    "Spotify",
                    new BigDecimal("9.99"),
                    "ENTERTAINMENT",
                    "MONTHLY",
                    (short) 1,
                    "",
                    LocalDate.now()
            );

            when(automaticTransactionRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(existing));
            when(walletService.getWallet(1L)).thenReturn(wallet);
            when(automaticTransactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            AutomaticTransactionModel result = automaticTransactionService.updateAutomaticTransaction(1L, dto);

            assertThat(result.getName()).isEqualTo("Spotify");
            assertThat(result.getRecurrence()).isEqualTo(TransactionRecurrence.MONTHLY);
        }
    }

    @Nested
    @DisplayName("deleteAutomaticTransaction")
    class DeleteAutomaticTransactionTests {

        @Test
        @DisplayName("should delete automatic transaction successfully")
        void shouldDeleteAutomaticTransactionSuccessfully() {
            AutomaticTransactionModel autoTx = new AutomaticTransactionModel();
            autoTx.setId(1L);

            when(automaticTransactionRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(autoTx));
            automaticTransactionService.deleteAutomaticTransaction(1L);

            verify(automaticTransactionRepository).delete(autoTx);
        }
    }

    @Nested
    @DisplayName("processAutomaticTransaction")
    class ProcessAutomaticTransactionTests {

        @Test
        @DisplayName("should process automatic transaction and update next date")
        void shouldProcessAutomaticTransactionSuccessfully() {
            WalletModel wallet = new WalletModel();
            wallet.setCurrency(Currency.EUR);

            AutomaticTransactionModel autoTx = AutomaticTransactionModel.builder()
                    .user(user)
                    .wallet(wallet)
                    .amount(BigDecimal.TEN)
                    .category(ExpenseCategory.FOOD)
                    .type(ExpenseType.EXPENSE)
                    .recurrence(TransactionRecurrence.MONTHLY)
                    .interval((short) 1)
                    .nextTransactionDate(LocalDate.of(2026, 1, 1))
                    .build();

            automaticTransactionService.processAutomaticTransaction(autoTx);

            assertThat(autoTx.getNextTransactionDate()).isEqualTo(LocalDate.of(2026, 2, 1));
            verify(transactionRepository).save(any(TransactionModel.class));
            verify(automaticTransactionRepository).save(autoTx);
        }

        @Test
        @DisplayName("should process all due automatic transactions")
        void shouldProcessDailyAutomaticTransactions() {
            when(automaticTransactionRepository
                    .findByNextTransactionDateLessThanEqual(any()))
                    .thenReturn(List.of(new AutomaticTransactionModel()));

            automaticTransactionService.processDailyAutomaticTransactions();

            verify(automaticTransactionRepository).findByNextTransactionDateLessThanEqual(any());
        }
    }
}
