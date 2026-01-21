package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.AutomaticTransactionModel;

import java.io.Serializable;
import java.time.LocalDate;

public record AutomaticTransactionSimple(
        Long id,
        String walletName,
        String name,
        String amount,
        String category,
        String type,
        LocalDate nextTransactionDate
) implements Serializable {
    public static AutomaticTransactionSimple fromEntity(
            AutomaticTransactionModel model
    ) {
        return new AutomaticTransactionSimple(
                model.getId(),
                model.getWallet().getName(),
                model.getName(),
                model.getWallet().getCurrency().format(model.getAmount()),
                model.getCategory().toString(),
                model.getType().toString(),
                model.getNextTransactionDate()
        );
    }
}