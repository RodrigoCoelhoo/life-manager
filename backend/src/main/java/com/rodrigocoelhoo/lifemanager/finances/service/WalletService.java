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
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
public class WalletService {

    private final UserService userService;
    private final WalletRepository walletRepository;
    private final RedisCacheService redisCacheService;

    private static final String CACHE_LIST = "wallets";

    public WalletService(
            UserService userService,
            WalletRepository walletRepository,
            RedisCacheService redisCacheService
    ) {
        this.userService = userService;
        this.walletRepository = walletRepository;
        this.redisCacheService = redisCacheService;
    }

    @Cacheable(value = CACHE_LIST, keyGenerator = "userAwareKeyGenerator")
    public Page<WalletResponseDTO> getWallets(Pageable pageable, String name) {
        UserModel user = userService.getLoggedInUser();
        Page<WalletModel> page;
        if(name == null || name.isBlank())
            page = walletRepository.findAllByUser(user, pageable);
        else
            page = walletRepository.findByUserAndNameContainingIgnoreCase(user, name, pageable);

        return page.map(WalletResponseDTO::fromEntity);
    }

    public WalletModel getWallet(Long id) {
        UserModel user = userService.getLoggedInUser();
        return walletRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new ResourceNotFound(
                        "Wallet with ID '" + id + "' doesn't belong to the current user")
                );
    }

    private Currency validateWalletCurrency(String currency) {
        try {
            return Currency.valueOf(currency.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(
                    "Currency '"+ currency.toUpperCase() +"' doesn't exist.\n"+
                            "Valid Currency: " + Currency.all()
            );
        }
    }

    private WalletType validateWalletType(String type) {
        try {
            return WalletType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(
                    "Type '"+ type.toUpperCase() +"' doesn't exist.\n"+
                            "Valid Type's: " + WalletType.all()
            );
        }
    }

    @Transactional
    public WalletModel createWallet(WalletDTO data) {
        UserModel user = userService.getLoggedInUser();

        WalletType type = validateWalletType(data.type());
        Currency currency = validateWalletCurrency(data.currency());

        WalletModel wallet = WalletModel.builder()
                .user(user)
                .name(data.name())
                .type(type)
                .balance(data.balance())
                .currency(currency)
                .build();

        WalletModel saved =  walletRepository.save(wallet);
        redisCacheService.evictUserCache(CACHE_LIST);
        redisCacheService.evictUserCacheSpecific("financesDashboard", "yearMonth:" + YearMonth.now() + "*");
        return saved;
    }

    @Transactional
    public WalletModel updateWallet(Long id, @Valid WalletUpdateDTO data) {
        WalletModel wallet = getWallet(id);

        WalletType type = validateWalletType(data.type());

        wallet.setName(data.name());
        wallet.setType(type);

        WalletModel saved =  walletRepository.save(wallet);
        redisCacheService.evictUserCache(CACHE_LIST);
        redisCacheService.evictUserCache("transactions");
        redisCacheService.evictUserCache("transferences");
        redisCacheService.evictUserCache("bills");
        redisCacheService.evictUserCacheSpecific("financesDashboard", "yearMonth:" + YearMonth.now() + "*");
        return saved;
    }

    @Transactional
    public void deleteWallet(Long id) {
        WalletModel wallet = getWallet(id);
        walletRepository.delete(wallet);

        redisCacheService.evictUserCache(CACHE_LIST);
        redisCacheService.evictUserCache("transactions");
        redisCacheService.evictUserCache("transferences");
        redisCacheService.evictUserCache("bills");
        redisCacheService.evictUserCacheSpecific("financesDashboard", "yearMonth:" + YearMonth.now() + "*");
    }
}
