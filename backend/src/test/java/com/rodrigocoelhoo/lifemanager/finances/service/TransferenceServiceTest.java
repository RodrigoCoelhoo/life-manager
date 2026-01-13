package com.rodrigocoelhoo.lifemanager.finances.service;

import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.finances.dto.TransferenceDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.Currency;
import com.rodrigocoelhoo.lifemanager.finances.model.TransferenceModel;
import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;
import com.rodrigocoelhoo.lifemanager.finances.repository.TransferenceRepository;
import com.rodrigocoelhoo.lifemanager.finances.repository.WalletRepository;
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

class TransferenceServiceTest {

    @InjectMocks
    private TransferenceService transferenceService;

    @Mock
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransferenceRepository transferenceRepository;

    @Mock
    private UserService userService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserModel();
        user.setId(1L);
        user.setUsername("testuser");
        when(userService.getLoggedInUser()).thenReturn(user);
    }

    /*@Nested
    @DisplayName("getAllTransferences")
    class GetAllTransferencesTests {

        @Test
        @DisplayName("should return all transferences for logged-in user")
        void shouldReturnAllTransferences() {
            TransferenceModel t1 = new TransferenceModel();
            TransferenceModel t2 = new TransferenceModel();

            Page<TransferenceModel> page = new PageImpl<>(List.of(t1, t2));
            when(transferenceRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            Page<TransferenceModel> result = transferenceService.getAllTransferences(Pageable.unpaged());

            assertThat(result.getContent()).hasSize(2);
            verify(transferenceRepository).findAllByUser(user, Pageable.unpaged());
        }
    }*/

    @Nested
    @DisplayName("getTransference")
    class GetTransferenceTests {

        @Test
        @DisplayName("should return transference when it belongs to user")
        void shouldReturnTransference() {
            TransferenceModel transference = new TransferenceModel();
            transference.setId(1L);

            when(transferenceRepository.findByUserAndId(user, 1L))
                    .thenReturn(Optional.of(transference));

            TransferenceModel result = transferenceService.getTransference(1L);

            assertThat(result.getId()).isEqualTo(1L);
            verify(transferenceRepository).findByUserAndId(user, 1L);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if transference does not belong to user")
        void shouldThrowIfTransferenceNotFound() {
            when(transferenceRepository.findByUserAndId(user, 1L))
                    .thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> transferenceService.getTransference(1L)
            );

            assertThat(exception.getMessage())
                    .contains("Transference with ID '1' doesn't belong to the current user");
        }
    }

    @Nested
    @DisplayName("get5RecentTransferences")
    class Get5RecentTransferencesTests {

        @Test
        @DisplayName("should return the 5 most recent transferences in date range")
        void shouldReturnRecentTransferences() {
            LocalDate start = LocalDate.now().minusDays(5);
            LocalDate end = LocalDate.now();

            List<TransferenceModel> transferences = List.of(
                    new TransferenceModel(),
                    new TransferenceModel()
            );

            when(transferenceRepository.findTop5ByUserAndDateBetweenOrderByDateDescIdDesc(user, start, end)).thenReturn(transferences);

            List<TransferenceModel> result = transferenceService.get5RecentTransferences(start, end);

            assertThat(result).hasSize(2);
            verify(transferenceRepository).findTop5ByUserAndDateBetweenOrderByDateDescIdDesc(user, start, end);
        }
    }

    @Nested
    @DisplayName("createTransference")
    class CreateTransferenceTests {

        @Test
        @DisplayName("should create transference successfully")
        void shouldCreateTransferenceSuccessfully() {
            WalletModel from = new WalletModel();
            from.setBalance(new BigDecimal(100));
            from.setCurrency(Currency.EUR);

            WalletModel to = new WalletModel();
            to.setBalance(new BigDecimal(20));
            to.setCurrency(Currency.EUR);

            TransferenceDTO dto = new TransferenceDTO(
                    1L,
                    2L,
                    new BigDecimal(30),
                    LocalDate.now(),
                    ""
            );

            when(walletService.getWallet(1L)).thenReturn(from);
            when(walletService.getWallet(2L)).thenReturn(to);
            when(transferenceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            TransferenceModel result = transferenceService.createTransference(dto);

            assertThat(from.getBalance()).isEqualTo(new BigDecimal(70));
            assertThat(to.getBalance()).isEqualByComparingTo(new BigDecimal(50));
            verify(transferenceRepository).save(any());
        }

        @Test
        @DisplayName("should throw BadRequestException when fromWallet equals toWallet")
        void shouldThrowWhenSameWallet() {
            TransferenceDTO dto = new TransferenceDTO(
                    1L,
                    1L,
                    new BigDecimal(10),
                    LocalDate.now(),
                    ""
            );

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> transferenceService.createTransference(dto)
            );

            assertThat(exception.getMessage()).contains("A wallet can't do a transference to itself");
        }

        @Test
        @DisplayName("should throw BadRequestException if fromWallet has insufficient balance")
        void shouldThrowIfFromWalletInsufficientBalance() {
            WalletModel from = new WalletModel();
            from.setId(1L);
            from.setBalance(new BigDecimal(10));

            WalletModel to = new WalletModel();
            to.setId(2L);
            to.setBalance(BigDecimal.ZERO);

            TransferenceDTO dto = new TransferenceDTO(
                    1L,
                    2L,
                    new BigDecimal(20),
                    LocalDate.now(),
                    ""
            );

            when(walletService.getWallet(1L)).thenReturn(from);
            when(walletService.getWallet(2L)).thenReturn(to);

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> transferenceService.createTransference(dto)
            );

            assertThat(exception.getMessage()).contains("doesn't have enough funds");
        }
    }

    @Nested
    @DisplayName("updateTransference")
    class UpdateTransferenceTests {

        @Test
        @DisplayName("should update transference successfully")
        void shouldUpdateTransferenceSuccessfully() {
            WalletModel oldFrom = new WalletModel();
            oldFrom.setBalance(new BigDecimal(50));
            oldFrom.setCurrency(Currency.EUR);

            WalletModel oldTo = new WalletModel();
            oldTo.setBalance(new BigDecimal(80));
            oldTo.setCurrency(Currency.EUR);

            TransferenceModel existing = TransferenceModel.builder()
                    .amount(new BigDecimal(20))
                    .fromWallet(oldFrom)
                    .toWallet(oldTo)
                    .build();

            WalletModel newFrom = new WalletModel();
            newFrom.setBalance(new BigDecimal(100));
            newFrom.setCurrency(Currency.EUR);

            WalletModel newTo = new WalletModel();
            newTo.setBalance(BigDecimal.ZERO);
            newTo.setCurrency(Currency.EUR);

            TransferenceDTO dto = new TransferenceDTO(
                    3L,
                    4L,
                    new BigDecimal(30),
                    LocalDate.now(),
                    ""
            );

            when(transferenceRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(existing));
            when(walletService.getWallet(3L)).thenReturn(newFrom);
            when(walletService.getWallet(4L)).thenReturn(newTo);
            when(transferenceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            TransferenceModel result = transferenceService.updateTransference(1L, dto);

            assertThat(oldFrom.getBalance()).isEqualTo(new BigDecimal(70));
            assertThat(oldTo.getBalance()).isEqualByComparingTo(new BigDecimal(60));
            assertThat(newFrom.getBalance()).isEqualTo(new BigDecimal(70));
            assertThat(newTo.getBalance()).isEqualByComparingTo(new BigDecimal(30));
            verify(transferenceRepository).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFound if transference does not exist")
        void shouldThrowIfTransferenceDoesNotExist() {
            TransferenceDTO dto = new TransferenceDTO(
                    1L,
                    2L,
                    new BigDecimal(10),
                    LocalDate.now(),
                    ""
            );

            when(transferenceRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> transferenceService.updateTransference(1L, dto)
            );

            assertThat(exception.getMessage()).contains("Transference with ID '1' doesn't belong to the current user");
        }

        @Test
        @DisplayName("should throw BadRequestException if old wallet revert causes negative balance")
        void shouldThrowIfRevertFails() {
            TransferenceDTO dto = new TransferenceDTO(
                    1L,
                    2L,
                    new BigDecimal(10),
                    LocalDate.now(),
                    ""
            );

            WalletModel oldFrom = new WalletModel();
            oldFrom.setBalance(new BigDecimal(0));
            oldFrom.setCurrency(Currency.EUR);
            oldFrom.setId(1L);

            WalletModel oldTo = new WalletModel();
            oldTo.setBalance(new BigDecimal(10));
            oldTo.setCurrency(Currency.EUR);
            oldTo.setId(2L);

            TransferenceModel existing = TransferenceModel.builder()
                    .amount(new BigDecimal(20))
                    .fromWallet(oldFrom)
                    .toWallet(oldTo)
                    .build();

            when(transferenceRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(existing));

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> transferenceService.updateTransference(1L, dto)
            );

            assertThat(exception.getMessage()).isEqualTo("Wallet with ID '2' doesn't have enough funds");
        }

        @Test
        @DisplayName("should throw BadRequestException if new wallet has insufficient balance")
        void shouldThrowIfNewWalletInsufficientBalance() {
            TransferenceDTO dto = new TransferenceDTO(
                    3L,
                    4L,
                    new BigDecimal(50),
                    LocalDate.now(),
                    ""
            );

            WalletModel oldFrom = new WalletModel();
            oldFrom.setId(1L);
            oldFrom.setBalance(new BigDecimal(30));
            oldFrom.setCurrency(Currency.EUR);

            WalletModel oldTo = new WalletModel();
            oldTo.setId(2L);
            oldTo.setBalance(new BigDecimal(100));
            oldTo.setCurrency(Currency.EUR);

            TransferenceModel existing = TransferenceModel.builder()
                    .amount(new BigDecimal(30))
                    .fromWallet(oldFrom)
                    .toWallet(oldTo)
                    .build();

            WalletModel newFrom = new WalletModel();
            newFrom.setId(3L);
            newFrom.setBalance(new BigDecimal(10));
            newFrom.setCurrency(Currency.EUR);

            WalletModel newTo = new WalletModel();
            newTo.setId(4L);
            newTo.setBalance(BigDecimal.ZERO);
            newTo.setCurrency(Currency.EUR);

            when(transferenceRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(existing));
            when(walletService.getWallet(3L)).thenReturn(newFrom);
            when(walletService.getWallet(4L)).thenReturn(newTo);

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> transferenceService.updateTransference(1L, dto)
            );

            assertThat(exception.getMessage()).isEqualTo("Wallet with ID '3' doesn't have enough funds");
            verify(transferenceRepository, never()).save(any());
        }

    }

    @Nested
    @DisplayName("deleteTransference")
    class DeleteTransferenceTests {

        @Test
        @DisplayName("should delete transference successfully")
        void shouldDeleteTransferenceSuccessfully() {
            WalletModel from = new WalletModel();
            from.setBalance(new BigDecimal(0));
            from.setCurrency(Currency.EUR);
            from.setId(1L);

            WalletModel to = new WalletModel();
            to.setBalance(new BigDecimal(10));
            to.setCurrency(Currency.EUR);
            to.setId(2L);

            TransferenceModel transference = new TransferenceModel();
            transference.setId(1L);
            transference.setAmount(new BigDecimal(10));
            transference.setFromWallet(from);
            transference.setToWallet(to);

            when(transferenceRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(transference));

            transferenceService.deleteTransference(1L);

            assertThat(from.getBalance()).isEqualTo(new BigDecimal(10));
            assertThat(to.getBalance()).isEqualByComparingTo(new BigDecimal(0));
            verify(transferenceRepository).delete(any(TransferenceModel.class));
        }

        @Test
        @DisplayName("should throw BadRequestException if revert causes negative balance")
        void shouldThrowIfNegativeBalanceOnDelete() {
            WalletModel from = new WalletModel();
            from.setBalance(new BigDecimal(0));
            from.setCurrency(Currency.EUR);
            from.setId(1L);

            WalletModel to = new WalletModel();
            to.setBalance(new BigDecimal(10));
            to.setCurrency(Currency.EUR);
            to.setId(2L);

            TransferenceModel transference = new TransferenceModel();
            transference.setId(1L);
            transference.setAmount(new BigDecimal(20));
            transference.setFromWallet(from);
            transference.setToWallet(to);

            when(transferenceRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(transference));

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> transferenceService.deleteTransference(1L)
            );

            assertThat(exception.getMessage()).isEqualTo("Wallet with ID '2' doesn't have enough funds");
            verify(transferenceRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFound if transference does not exist")
        void shouldThrowIfTransferenceDoesNotExist() {
            when(transferenceRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> transferenceService.deleteTransference(1L)
            );

            assertThat(exception.getMessage()).isEqualTo("Transference with ID '1' doesn't belong to the current user");
            verify(transferenceRepository, never()).save(any());
        }
    }
}
