package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.Currency;
import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;
import com.rodrigocoelhoo.lifemanager.finances.model.WalletType;

import java.io.Serializable;

public record WalletResponseDTO(
    Long id,
    String name,
    WalletType type,
    String balance,
    Currency currency
) implements Serializable {
    public static WalletResponseDTO fromEntity(WalletModel model) {
        return new WalletResponseDTO(
                model.getId(),
                model.getName(),
                model.getType(),
                model.getCurrency().format(model.getBalance()),
                model.getCurrency()
        );
    }
}
