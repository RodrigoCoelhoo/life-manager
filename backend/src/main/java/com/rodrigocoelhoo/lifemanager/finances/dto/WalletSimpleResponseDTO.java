package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;

import java.io.Serializable;

public record WalletSimpleResponseDTO(
        Long id,
        String name
) implements Serializable {
    public static WalletSimpleResponseDTO fromEntity(WalletModel model) {
        return new WalletSimpleResponseDTO(
                model.getId(),
                model.getName()
        );
    }
}
