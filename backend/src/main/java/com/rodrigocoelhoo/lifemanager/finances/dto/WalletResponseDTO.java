package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;

public record WalletResponseDTO(
    Long id,
    String name,
    String type,
    String balance,
    String currency
) {
    public static WalletResponseDTO fromEntity(WalletModel model) {
        return new WalletResponseDTO(
                model.getId(),
                model.getName(),
                model.getType().toString(),
                model.getCurrency().format(model.getBalance()),
                model.getCurrency().toString()
        );
    }
}
