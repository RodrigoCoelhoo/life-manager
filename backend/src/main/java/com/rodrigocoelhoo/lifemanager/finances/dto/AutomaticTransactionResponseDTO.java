package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.AutomaticTransactionModel;
import com.rodrigocoelhoo.lifemanager.finances.model.TransactionRecurrence;

import java.time.LocalDate;

public record AutomaticTransactionResponseDTO(
    Long id,
    WalletSimpleResponseDTO wallet,
    String name,
    String amount,
    String category,
    String type,
    TransactionRecurrence recurrence,
    short interval,
    String description,
    LocalDate nextTransactionDate

) {
    public static AutomaticTransactionResponseDTO fromEntity(
            AutomaticTransactionModel model
    ) {
        return new AutomaticTransactionResponseDTO(
                model.getId(),
                WalletSimpleResponseDTO.fromEntity(model.getWallet()),
                model.getName(),
                model.getWallet().getCurrency().format(model.getAmount()),
                model.getCategory().toString(),
                model.getType().toString(),
                model.getRecurrence(),
                model.getInterval(),
                model.getDescription(),
                model.getNextTransactionDate()
        );
    }
}
