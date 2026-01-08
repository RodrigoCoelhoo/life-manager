package com.rodrigocoelhoo.lifemanager.finances.service;

import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.finances.dto.MonthOverviewDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.*;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private WalletService walletService;

    @Mock
    private TransferenceService transferenceService;

    @Mock
    private UserService userService;

    @Mock
    private AutomaticTransactionService automaticTransactionService;

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
    @DisplayName("getMonthOverview")
    class GetMonthOverviewTests {

        @Test
        @DisplayName("should return month overview with correct totals")
        void shouldReturnMonthOverviewCorrectly() {
            YearMonth yearMonth = YearMonth.of(2026, 1);
            Currency currency = Currency.AUD;

            TransactionModel t1 = new TransactionModel();
            t1.setAmount(new BigDecimal(100));
            t1.setCategory(ExpenseCategory.FOOD);
            t1.setType(ExpenseType.EXPENSE);
            t1.setDate(LocalDate.of(2026, 1, 5));
            t1.setCurrency(Currency.AUD);

            TransactionModel t2 = new TransactionModel();
            t2.setAmount(new BigDecimal(200));
            t2.setCategory(ExpenseCategory.SALARY);
            t2.setType(ExpenseType.INCOME);
            t2.setDate(LocalDate.of(2026, 1, 10));
            t2.setCurrency(Currency.AUD);

            WalletModel txWallet = new WalletModel();
            txWallet.setBalance(new BigDecimal(500));
            txWallet.setCurrency(Currency.AUD);
            txWallet.setType(WalletType.BANK);
            txWallet.setName("Test Wallet");

            t1.setWallet(txWallet);
            t2.setWallet(txWallet);


            List<TransactionModel> transactions = List.of(t1, t2);

            when(transactionService.getTransactionsByRange(
                    yearMonth.atDay(1),
                    yearMonth.atEndOfMonth()
            )).thenReturn(transactions);

            WalletModel wallet = new WalletModel();
            wallet.setBalance(new BigDecimal(500));
            wallet.setType(WalletType.BANK);
            wallet.setName("Test");
            wallet.setCurrency(Currency.EUR);

            when(walletService.getWallets(any(), any())).thenReturn(new PageImpl<>(List.of(wallet)));

            when(transferenceService.get5RecentTransferences(
                    yearMonth.atDay(1),
                    yearMonth.atEndOfMonth()
            )).thenReturn(List.of());

            when(automaticTransactionService.get5NextAutomaticTransaction()).thenReturn(List.of());

            MonthOverviewDTO result = dashboardService.getMonthOverview(yearMonth, currency);

            assertThat(result).isNotNull();
            assertThat(result.totalIncome()).isEqualTo(currency.format(new BigDecimal(200)));
            assertThat(result.totalExpenses()).isEqualTo(currency.format(new BigDecimal(100)));
            assertThat(result.netBalance()).isEqualTo(currency.format(new BigDecimal(100)));

            assertThat(result.recentTransactions()).hasSize(2);
            assertThat(result.wallets()).hasSize(1);
            verify(transactionService).getTransactionsByRange(yearMonth.atDay(1), yearMonth.atEndOfMonth());
            verify(walletService).getWallets(any(), any());
            verify(transferenceService).get5RecentTransferences(yearMonth.atDay(1), yearMonth.atEndOfMonth());
            verify(automaticTransactionService).get5NextAutomaticTransaction();
        }

        @Test
        @DisplayName("should calculate previous months net balance")
        void shouldCalculatePreviousMonthsNetBalance() {
            YearMonth currentMonth = YearMonth.now();
            Currency currency = Currency.AUD;

            WalletModel wallet = new WalletModel();
            wallet.setCurrency(Currency.AUD);

            TransactionModel pastTx = new TransactionModel();
            pastTx.setAmount(new BigDecimal(50));
            pastTx.setCategory(ExpenseCategory.FOOD);
            pastTx.setDate(currentMonth.minusMonths(1).atDay(15));
            pastTx.setCurrency(Currency.AUD);
            pastTx.setWallet(wallet);

            when(walletService.getWallets(any(), any())).thenReturn(new PageImpl<>(List.of()));
            when(transactionService.getTransactionsByRange(
                    currentMonth.atDay(1),
                    currentMonth.atEndOfMonth()
            )).thenReturn(List.of());

            when(transactionService.getTransactionsByRange(
                    currentMonth.minusMonths(5).atDay(1),
                    currentMonth.minusMonths(1).atEndOfMonth()
            )).thenReturn(List.of(pastTx));

            MonthOverviewDTO result = dashboardService.getMonthOverview(currentMonth, currency);

            assertThat(result.previousMonthsNetBalance()).isNotEmpty();
        }


        @Test
        @DisplayName("should handle empty transactions")
        void shouldHandleEmptyTransactions() {
            YearMonth yearMonth = YearMonth.of(2026, 1);
            Currency currency = Currency.AUD;

            when(transactionService.getTransactionsByRange(any(), any())).thenReturn(List.of());
            when(walletService.getWallets(any(), any())).thenReturn(new PageImpl<>(List.of()));
            when(transferenceService.get5RecentTransferences(any(), any())).thenReturn(List.of());
            when(automaticTransactionService.get5NextAutomaticTransaction()).thenReturn(List.of());

            MonthOverviewDTO result = dashboardService.getMonthOverview(yearMonth, currency);

            assertThat(result.totalIncome()).isEqualTo(currency.format(BigDecimal.ZERO));
            assertThat(result.totalExpenses()).isEqualTo(currency.format(BigDecimal.ZERO));
            assertThat(result.netBalance()).isEqualTo(currency.format(BigDecimal.ZERO));
            assertThat(result.recentTransactions()).isEmpty();
            assertThat(result.wallets()).isEmpty();
        }

        @Test
        @DisplayName("should limit recent transactions to 5 items")
        void shouldLimitRecentTransactions() {
            YearMonth yearMonth = YearMonth.of(2026, 1);
            Currency currency = Currency.AUD;

            List<TransactionModel> transactions = List.of(
                    new TransactionModel(), new TransactionModel(), new TransactionModel(),
                    new TransactionModel(), new TransactionModel(), new TransactionModel(),
                    new TransactionModel(), new TransactionModel(), new TransactionModel(),
                    new TransactionModel()
            );

            WalletModel wallet = new WalletModel();
            wallet.setName("Test");
            wallet.setType(WalletType.BANK);
            wallet.setCurrency(Currency.AUD);
            wallet.setBalance(new BigDecimal(10000000));

            transactions.forEach(tx -> {
                tx.setWallet(wallet);
                tx.setCurrency(Currency.AUD);
                tx.setCategory(ExpenseCategory.FOOD);
                tx.setType(ExpenseType.EXPENSE);
                tx.setAmount(new BigDecimal(10));
                tx.setDate(LocalDate.of(2026, 1, 5));
            });

            when(transactionService.getTransactionsByRange(yearMonth.atDay(1), yearMonth.atEndOfMonth())).thenReturn(transactions);
            when(walletService.getWallets(any(), any())).thenReturn(new PageImpl<>(List.of()));
            when(transferenceService.get5RecentTransferences(any(), any())).thenReturn(List.of());
            when(automaticTransactionService.get5NextAutomaticTransaction()).thenReturn(List.of());

            MonthOverviewDTO result = dashboardService.getMonthOverview(yearMonth, currency);

            assertThat(result.recentTransactions()).hasSize(5);
        }
    }
}
