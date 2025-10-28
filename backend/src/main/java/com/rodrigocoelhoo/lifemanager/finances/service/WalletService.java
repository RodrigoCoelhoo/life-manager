package com.rodrigocoelhoo.lifemanager.finances.service;

import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.finances.dto.WalletDTO;
import com.rodrigocoelhoo.lifemanager.finances.dto.WalletUpdateDTO;
import com.rodrigocoelhoo.lifemanager.finances.model.Currency;
import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;
import com.rodrigocoelhoo.lifemanager.finances.model.WalletType;
import com.rodrigocoelhoo.lifemanager.finances.repository.WalletRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private final UserService userService;
    private final WalletRepository walletRepository;

    public WalletService(
            UserService userService,
            WalletRepository walletRepository
    ) {
        this.userService = userService;
        this.walletRepository = walletRepository;
    }

    public List<WalletModel> getAllWallets() {
        UserModel user = userService.getLoggedInUser();
        return walletRepository.findAllByUser(user);
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

        return walletRepository.save(wallet);
    }

    @Transactional
    public WalletModel updateWallet(Long id, @Valid WalletUpdateDTO data) {
        WalletModel wallet = getWallet(id);

        WalletType type = validateWalletType(data.type());
        Currency currency = validateWalletCurrency(data.currency());
        BigDecimal balance = wallet.getCurrency().equals(currency) ?
                wallet.getBalance() :
                wallet.getCurrency().convertTo(wallet.getBalance(), currency);

        wallet.setName(data.name());
        wallet.setType(type);
        wallet.setBalance(balance);
        wallet.setCurrency(currency);

        return walletRepository.save(wallet);
    }

    @Transactional
    public void deleteWallet(Long id) {
        WalletModel wallet = getWallet(id);
        walletRepository.delete(wallet);
    }
}
