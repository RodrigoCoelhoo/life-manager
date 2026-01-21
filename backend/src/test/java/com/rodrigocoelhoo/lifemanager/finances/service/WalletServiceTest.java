package com.rodrigocoelhoo.lifemanager.finances.service;

import com.rodrigocoelhoo.lifemanager.config.RedisCacheService;
import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.finances.dto.WalletDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.WalletResponseDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.WalletUpdateDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.Currency;
import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;
import com.rodrigocoelhoo.lifemanager.finances.model.WalletType;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private UserService userService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private RedisCacheService redisCacheService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserModel();
        user.setId(1L);
        user.setUsername("testuser");
        when(userService.getLoggedInUser()).thenReturn(user);
        doNothing().when(redisCacheService).evictUserCache(anyString());
        doNothing().when(redisCacheService).evictUserCacheSpecific(anyString(), anyString());
    }

    @Nested
    @DisplayName("getWallets")
    class GetWalletsTests {

        @Test
        @DisplayName("should return all wallets when name is null")
        void shouldReturnAllWallets() {
            WalletModel w1 = new WalletModel();
            w1.setBalance(new BigDecimal(10));
            w1.setCurrency(Currency.EUR);

            WalletModel w2 = new WalletModel();
            w2.setBalance(new BigDecimal(10));
            w2.setCurrency(Currency.EUR);

            Page<WalletModel> page = new PageImpl<>(List.of(w1, w2));
            when(walletRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            Page<WalletResponseDTO> result = walletService.getWallets(Pageable.unpaged(), null);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder(WalletResponseDTO.fromEntity(w1), WalletResponseDTO.fromEntity(w2));

            verify(walletRepository).findAllByUser(user, Pageable.unpaged());
            verify(walletRepository, never()).findByUserAndNameContainingIgnoreCase(any(), any(), any());
        }

        @Test
        @DisplayName("should return filtered wallets by name")
        void shouldReturnWalletsFilteredByName() {
            WalletModel w1 = new WalletModel();
            w1.setName("Caixa Geral de Depositos");
            w1.setType(WalletType.BANK);
            w1.setBalance(new BigDecimal(10));
            w1.setCurrency(Currency.EUR);

            WalletModel w2 = new WalletModel();
            w2.setName("Santander");
            w2.setType(WalletType.BANK);
            w2.setBalance(new BigDecimal(10));
            w2.setCurrency(Currency.EUR);

            Page<WalletModel> page = new PageImpl<>(List.of(w1));
            when(walletRepository.findByUserAndNameContainingIgnoreCase(user, "Caixa" ,Pageable.unpaged())).thenReturn(page);

            Page<WalletResponseDTO> result = walletService.getWallets(Pageable.unpaged(), "Caixa");

            assertThat(result).hasSize(1);
            assertThat(result).containsExactlyInAnyOrder(WalletResponseDTO.fromEntity(w1));

            verify(walletRepository, never()).findAllByUser(any(), any());
            verify(walletRepository).findByUserAndNameContainingIgnoreCase(user, "Caixa" ,Pageable.unpaged());
        }
    }

    @Nested
    @DisplayName("getWallet")
    class GetWalletTests {

        @Test
        @DisplayName("should return wallet when it belongs to user")
        void shouldReturnWallet() {
            WalletModel wallet = new WalletModel();
            wallet.setName("Santander");
            wallet.setId(1L);

            when(walletRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(wallet));

            WalletModel result = walletService.getWallet(1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Santander");
            assertThat(result.getId()).isEqualTo(1L);

            verify(walletRepository).findByUserAndId(user, 1L);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if wallet does not belong to user")
        void shouldThrowIfWalletNotFound() {
            when(walletRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            ResourceNotFound ex = assertThrows(ResourceNotFound.class, () -> walletService.getWallet(1L));

            assertThat(ex.getMessage()).contains("Wallet with ID '1' doesn't belong to the current user");
        }
    }

    @Nested
    @DisplayName("createWallet")
    class CreateWalletTests {

        @Test
        @DisplayName("should create wallet successfully")
        void shouldCreateWalletSuccessfully() {
            WalletDTO walletDTO = new WalletDTO("Santander", "BANK", new BigDecimal(10), "EUR");

            when(walletRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            WalletModel result = walletService.createWallet(walletDTO);

            assertThat(result.getName()).isEqualTo("Santander");
            assertThat(result.getType()).isEqualTo(WalletType.BANK);
            assertThat(result.getBalance()).isEqualTo(walletDTO.balance());
            assertThat(result.getCurrency()).isEqualTo(Currency.EUR);

            verify(walletRepository).save(any(WalletModel.class));
        }

        @Test
        @DisplayName("should throw BadRequestException for invalid currency")
        void shouldThrowForInvalidCurrency() {
            WalletDTO walletDTO = new WalletDTO("Santander", "BANK", new BigDecimal(10), "YMCA");

            BadRequestException ex = assertThrows(BadRequestException.class, () -> walletService.createWallet(walletDTO));

            assertThat(ex.getMessage()).isEqualTo(
                    "Currency 'YMCA' doesn't exist.\nValid Currency: " + Currency.all()
            );
        }

        @Test
        @DisplayName("should throw BadRequestException for invalid wallet type")
        void shouldThrowForInvalidWalletType() {
            WalletDTO walletDTO = new WalletDTO("Santander", "YMCA", new BigDecimal(10), "EUR");

            BadRequestException ex = assertThrows(BadRequestException.class, () -> walletService.createWallet(walletDTO));

            assertThat(ex.getMessage()).isEqualTo(
                    "Type 'YMCA' doesn't exist.\nValid Type's: " + WalletType.all()
            );
        }
    }

    @Nested
    @DisplayName("updateWallet")
    class UpdateWalletTests {

        @Test
        @DisplayName("should update wallet successfully")
        void shouldUpdateWalletSuccessfully() {
            WalletUpdateDTO walletDTO = new WalletUpdateDTO("Santander", "BANK");
            WalletModel existing = WalletModel.builder()
                    .id(1L)
                    .name("Caixa Geral de Depositos")
                    .type(WalletType.CASH)
                    .balance(new BigDecimal(10))
                    .currency(Currency.EUR)
                    .build();

            when(walletRepository.findByUserAndId(user, 1L)).thenReturn(Optional.ofNullable(existing));
            when(walletRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            WalletModel result = walletService.updateWallet(1L, walletDTO);

            assertThat(result.getName()).isEqualTo("Santander");
            assertThat(result.getType()).isEqualTo(WalletType.BANK);
            Assertions.assertNotNull(existing);
            assertThat(result.getBalance()).isEqualTo(existing.getBalance());
            assertThat(result.getCurrency()).isEqualTo(existing.getCurrency());

            verify(walletRepository).save(any(WalletModel.class));
        }

        @Test
        @DisplayName("should throw BadRequestException for invalid wallet type on update")
        void shouldThrowForInvalidTypeOnUpdate() {
            WalletUpdateDTO walletDTO = new WalletUpdateDTO("Santander", "YMCA");
            WalletModel existing = WalletModel.builder()
                    .id(1L)
                    .name("Caixa Geral de Depositos")
                    .type(WalletType.CASH)
                    .balance(new BigDecimal(10))
                    .currency(Currency.EUR)
                    .build();

            when(walletRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(existing));

            BadRequestException ex = assertThrows(BadRequestException.class, () -> walletService.updateWallet(1L, walletDTO));
            assertThat(ex.getMessage()).isEqualTo(
                    "Type 'YMCA' doesn't exist.\nValid Type's: " + WalletType.all()
            );

            verify(walletRepository, never()).save(any(WalletModel.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFound if wallet does not exist")
        void shouldThrowIfWalletDoesNotExist() {
            WalletUpdateDTO walletDTO = new WalletUpdateDTO("Santander", "BANK");
            when(walletRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            ResourceNotFound ex = assertThrows(ResourceNotFound.class, () -> walletService.updateWallet(1L, walletDTO));
            assertThat(ex.getMessage()).isEqualTo(
                    "Wallet with ID '1' doesn't belong to the current user"
            );

            verify(walletRepository, never()).save(any(WalletModel.class));
        }
    }

    @Nested
    @DisplayName("deleteWallet")
    class DeleteWalletTests {

        @Test
        @DisplayName("should delete wallet successfully")
        void shouldDeleteWalletSuccessfully() {
            WalletModel wallet = new WalletModel();
            when(walletRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(wallet));

            walletService.deleteWallet(1L);
            verify(walletRepository).delete(any(WalletModel.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFound if wallet does not exist")
        void shouldThrowIfWalletDoesNotExist() {
            when(walletRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            ResourceNotFound ex = assertThrows(ResourceNotFound.class, () -> walletService.deleteWallet(1L));
            assertThat(ex.getMessage()).isEqualTo(
                    "Wallet with ID '1' doesn't belong to the current user"
            );

            verify(walletRepository, never()).delete(any(WalletModel.class));
        }
    }
}
