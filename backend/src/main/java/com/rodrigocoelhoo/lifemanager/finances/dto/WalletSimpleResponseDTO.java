package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;

public record WalletSimpleResponseDTO(
        Long id,
        String name
) {
    public static WalletSimpleResponseDTO fromEntity(WalletModel model) {
        return new WalletSimpleResponseDTO(
                model.getId(),
                model.getName()
        );
    }
}
