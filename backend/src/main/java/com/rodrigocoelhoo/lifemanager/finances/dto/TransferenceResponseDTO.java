package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.TransferenceModel;
import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferenceResponseDTO(
    Long id,
    WalletSimpleResponseDTO fromWallet,
    String fromAmount,
    WalletSimpleResponseDTO toWallet,
    String toAmount,
    LocalDate date,
    String description
) {
    public static TransferenceResponseDTO fromEntity(TransferenceModel model) {
        WalletModel fromWallet = model.getFromWallet();
        WalletModel toWallet = model.getToWallet();
        BigDecimal toAmount = fromWallet.getCurrency().convertTo(model.getAmount(), toWallet.getCurrency());

        return new TransferenceResponseDTO(
                model.getId(),
                WalletSimpleResponseDTO.fromEntity(fromWallet),
                fromWallet.getCurrency().format(model.getAmount()),
                WalletSimpleResponseDTO.fromEntity(toWallet),
                toWallet.getCurrency().format(toAmount),
                model.getDate(),
                model.getDescription()
        );
    }
}
