package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseCategory;
import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseType;
import com.rodrigocoelhoo.lifemanager.finances.model.TransactionModel;
import com.rodrigocoelhoo.lifemanager.finances.model.WalletModel;

import java.io.Serializable;
import java.time.LocalDate;

public record TransactionResponseDTO(
    Long id,
    WalletResponseDTO wallet,
    String amount,
    ExpenseCategory category,
    ExpenseType type,
    String description,
    LocalDate date
) implements Serializable {
    public static TransactionResponseDTO fromEntity(TransactionModel model) {
        WalletModel wallet = model.getWallet();

        return new TransactionResponseDTO(
                model.getId(),
                WalletResponseDTO.fromEntity(wallet),
                wallet.getCurrency().format(model.getAmount()),
                model.getCategory(),
                model.getType(),
                model.getDescription(),
                model.getDate()
        );
    }

    public static TransactionResponseDTO fromEntity(TransactionInternalDTO model) {
        return new TransactionResponseDTO(
                model.id(),
                model.wallet(),
                model.wallet().currency().format(model.amount()),
                model.category(),
                model.type(),
                model.description(),
                model.date()
        );
    }
}
