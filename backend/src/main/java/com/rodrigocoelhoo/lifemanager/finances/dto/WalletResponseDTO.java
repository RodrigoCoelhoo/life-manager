package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;

import java.math.BigDecimal;

public record WalletResponseDTO(
    Long id,
    String name,
    String type,
    BigDecimal balance,
    String currency
) {
    public static WalletResponseDTO fromEntity(WalletModel model) {
        return new WalletResponseDTO(
                model.getId(),
                model.getName(),
                model.getType().toString(),
                model.getBalance(),
                model.getCurrency().toString()
        );
    }
}
