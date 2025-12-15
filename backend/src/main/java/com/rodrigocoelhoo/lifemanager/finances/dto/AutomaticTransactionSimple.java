package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.AutomaticTransactionModel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AutomaticTransactionSimple(
        Long id,
        String walletName,
        String name,
        BigDecimal amount,
        String category,
        String type,
        LocalDate nextTransactionDate

) {
    public static AutomaticTransactionSimple fromEntity(
            AutomaticTransactionModel model
    ) {
        return new AutomaticTransactionSimple(
                model.getId(),
                model.getWallet().getName(),
                model.getName(),
                model.getAmount(),
                model.getCategory().toString(),
                model.getType().toString(),
                model.getNextTransactionDate()
        );
    }
}