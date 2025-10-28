package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.AutomaticTransactionModel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AutomaticTransactionResponseDTO(
    Long automaticTransactionId,
    String walletName,
    BigDecimal amount,
    String category,
    String type,
    String recurrence,
    short interval,
    String description,
    LocalDate nextTransactionDate

) {
    public static AutomaticTransactionResponseDTO fromEntity(
            AutomaticTransactionModel model
    ) {
        return new AutomaticTransactionResponseDTO(
                model.getId(),
                model.getWallet().getName(),
                model.getAmount(),
                model.getCategory().toString(),
                model.getType().toString(),
                model.getRecurrence().toString(),
                model.getInterval(),
                model.getDescription(),
                model.getNextTransactionDate()
        );
    }
}
