package com.rodrigocoelhoo.lifemanager.finances.service;

import com.rodrigocoelhoo.lifemanager.config.RedisCacheService;
import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.finances.dto.TransferenceDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.TransferenceResponseDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.Currency;
import com.rodrigocoelhoo.lifemanager.finances.model.TransferenceModel;
import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;
import com.rodrigocoelhoo.lifemanager.finances.repository.TransferenceRepository;
import com.rodrigocoelhoo.lifemanager.finances.repository.WalletRepository;
import com.rodrigocoelhoo.lifemanager.finances.specification.TransferenceSpecification;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class TransferenceService {

    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final TransferenceRepository transferenceRepository;
    private final UserService userService;
    private final RedisCacheService redisCacheService;

    private static final String CACHE_LIST = "transferences";

    public TransferenceService(
            WalletService walletService,
            WalletRepository walletRepository,
            TransferenceRepository transferenceRepository,
            UserService userService,
            RedisCacheService redisCacheService
    ) {
        this.walletService = walletService;
        this.walletRepository = walletRepository;
        this.transferenceRepository = transferenceRepository;
        this.userService = userService;
        this.redisCacheService = redisCacheService;
    }

    @Cacheable(value = CACHE_LIST, keyGenerator = "userAwareKeyGenerator")
    public Page<TransferenceResponseDTO> getAllTransferences(
            Pageable pageable,
            Long sender,
            Long receiver,
            LocalDate startDate,
            LocalDate endDate
    ) {
        UserModel user = userService.getLoggedInUser();

        Specification<TransferenceModel> spec =
                TransferenceSpecification.withFilters(
                        user,
                        sender,
                        receiver,
                        startDate,
                        endDate
                );

        return transferenceRepository.findAll(spec, pageable).map(TransferenceResponseDTO::fromEntity);
    }

    public TransferenceModel getTransference(Long id) {
        UserModel user = userService.getLoggedInUser();
        return transferenceRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new ResourceNotFound("Transference with ID '"+ id + "' doesn't belong to the current user"));
    }

    @Cacheable(
            value = CACHE_LIST,
            key = "T(com.rodrigocoelhoo.lifemanager.config.RedisCacheService).getCurrentUsername() + " +
                    "'::recent5:' + #start + '|' + #end"
    )
    public List<TransferenceResponseDTO> get5RecentTransferences(
            LocalDate start,
            LocalDate end
    ) {
        UserModel user = userService.getLoggedInUser();
        return transferenceRepository.findTop5ByUserAndDateBetweenOrderByDateDescIdDesc(user, start, end)
                .stream().map(TransferenceResponseDTO::fromEntity)
                .toList();
    }

    private void revertWalletBalance(
            WalletModel fromWallet,
            WalletModel toWallet,
            BigDecimal amount
    ) {
        Currency fromWalletCurrency = fromWallet.getCurrency();
        Currency toWalletCurrency = toWallet.getCurrency();

        fromWallet.setBalance(fromWallet.getBalance().add(amount));

        BigDecimal convertedAmount = fromWalletCurrency.convertTo(amount, toWalletCurrency);

        toWallet.setBalance(toWallet.getBalance().subtract(convertedAmount));

        if (toWallet.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException(
                    "Wallet with ID '" + toWallet.getId() + "' doesn't have enough funds"
            );
        }
    }

    private void adjustWalletBalance(
            WalletModel fromWallet,
            WalletModel toWallet,
            BigDecimal amount
    ) {
        Currency fromWalletCurrency = fromWallet.getCurrency();
        Currency toWalletCurrency = toWallet.getCurrency();

        fromWallet.setBalance(fromWallet.getBalance().subtract(amount));

        if (fromWallet.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException(
                    "Wallet with ID '" + fromWallet.getId() + "' doesn't have enough funds"
            );
        }

        BigDecimal convertedAmount = fromWalletCurrency.convertTo(amount, toWalletCurrency);

        toWallet.setBalance(toWallet.getBalance().add(convertedAmount));
    }


    @Transactional
    public TransferenceModel createTransference(TransferenceDTO data) {
        if(data.fromWalletId().equals(data.toWalletId()))
            throw new BadRequestException("A wallet can't do a transference to itself");

        WalletModel fromWallet = walletService.getWallet(data.fromWalletId());
        WalletModel toWallet = walletService.getWallet(data.toWalletId());

        TransferenceModel transference = TransferenceModel.builder()
                .user(userService.getLoggedInUser())
                .fromWallet(fromWallet)
                .toWallet(toWallet)
                .amount(data.amount())
                .date(data.date())
                .description(data.description())
                .build();

        adjustWalletBalance(fromWallet, toWallet, data.amount());

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        TransferenceModel saved = transferenceRepository.save(transference);

        redisCacheService.evictUserCache(CACHE_LIST);
        redisCacheService.evictUserCacheSpecific("wallets", "wallet:" + fromWallet.getId());
        redisCacheService.evictUserCacheSpecific("wallets", "wallet:" + toWallet.getId());
        YearMonth yearMonth = YearMonth.from(transference.getDate());
        redisCacheService.evictUserCacheSpecific("financesDashboard", "yearMonth:" + yearMonth + "*");
        redisCacheService.evictUserCacheSpecific("financesDashboard", "yearMonth:" + YearMonth.now() + "*");

        return saved;
    }

    @Transactional
    public TransferenceModel updateTransference(Long id, TransferenceDTO data) {
        TransferenceModel transference = getTransference(id);
        WalletModel oldFrom = transference.getFromWallet();
        WalletModel oldTo = transference.getToWallet();

        revertWalletBalance(oldFrom, oldTo, transference.getAmount());

        WalletModel newFrom = walletService.getWallet(data.fromWalletId());
        WalletModel newTo = walletService.getWallet(data.toWalletId());

        adjustWalletBalance(newFrom, newTo, data.amount());

        transference.setFromWallet(newFrom);
        transference.setToWallet(newTo);
        transference.setAmount(data.amount());
        transference.setDate(data.date());
        transference.setDescription(data.description());

        Set<WalletModel> affectedWallets = new HashSet<>(List.of(oldFrom, oldTo, newFrom, newTo));
        affectedWallets.forEach(walletRepository::save);

        TransferenceModel saved = transferenceRepository.save(transference);

        redisCacheService.evictUserCache(CACHE_LIST);
        redisCacheService.evictUserCacheSpecific("wallets", "wallet:" + oldFrom.getId());
        redisCacheService.evictUserCacheSpecific("wallets", "wallet:" + oldTo.getId());
        redisCacheService.evictUserCacheSpecific("wallets", "wallet:" + newFrom.getId());
        redisCacheService.evictUserCacheSpecific("wallets", "wallet:" + newTo.getId());
        YearMonth yearMonth = YearMonth.from(transference.getDate());
        redisCacheService.evictUserCacheSpecific("financesDashboard", "yearMonth:" + yearMonth + "*");
        redisCacheService.evictUserCacheSpecific("financesDashboard", "yearMonth:" + YearMonth.now() + "*");

        return saved;
    }

    @Transactional
    public void deleteTransference(Long id) {
        TransferenceModel transference = getTransference(id);
        WalletModel fromWallet = transference.getFromWallet();
        WalletModel toWallet = transference.getToWallet();

        revertWalletBalance(fromWallet, toWallet, transference.getAmount());

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        transferenceRepository.delete(transference);

        redisCacheService.evictUserCache(CACHE_LIST);
        redisCacheService.evictUserCacheSpecific("wallets", "wallet:" + fromWallet.getId());
        redisCacheService.evictUserCacheSpecific("wallets", "wallet:" + toWallet.getId());
        YearMonth yearMonth = YearMonth.from(transference.getDate());
        redisCacheService.evictUserCacheSpecific("financesDashboard", "yearMonth:" + yearMonth + "*");
        redisCacheService.evictUserCacheSpecific("financesDashboard", "yearMonth:" + YearMonth.now() + "*");
    }
}
