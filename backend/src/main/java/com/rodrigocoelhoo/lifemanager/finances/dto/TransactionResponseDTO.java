package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.TransactionModel;
import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;

import java.time.LocalDate;

public record TransactionResponseDTO(
    Long id,
    WalletResponseDTO wallet,
    String amount,
    String category,
    String type,
    String description,
    LocalDate date
) {
    public static TransactionResponseDTO fromEntity(TransactionModel model) {
        WalletModel wallet = model.getWallet();

        return new TransactionResponseDTO(
                model.getId(),
                WalletResponseDTO.fromEntity(wallet),
                wallet.getCurrency().format(model.getAmount()),
                model.getCategory().toString(),
                model.getType().toString(),
                model.getDescription(),
                model.getDate()
        );
    }
}
