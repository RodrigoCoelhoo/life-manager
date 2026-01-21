package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionInternalDTO(
        Long id,
        WalletResponseDTO wallet,
        BigDecimal amount,
        Currency currency,
        ExpenseCategory category,
        ExpenseType type,
        LocalDate date,
        String description
) implements Serializable {
    public static TransactionInternalDTO fromEntity(TransactionModel model) {
        return new TransactionInternalDTO(
                model.getId(),
                WalletResponseDTO.fromEntity(model.getWallet()),
                model.getAmount(),
                model.getCurrency(),
                model.getCategory(),
                model.getType(),
                model.getDate(),
                model.getDescription()
        );
    }
}
