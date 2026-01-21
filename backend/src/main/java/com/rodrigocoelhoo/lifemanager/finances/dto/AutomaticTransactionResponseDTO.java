package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.AutomaticTransactionModel;
import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseCategory;
import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseType;
import com.rodrigocoelhoo.lifemanager.finances.model.TransactionRecurrence;

import java.io.Serializable;
import java.time.LocalDate;

public record AutomaticTransactionResponseDTO(
    Long id,
    WalletSimpleResponseDTO wallet,
    String name,
    String amount,
    ExpenseCategory category,
    ExpenseType type,
    TransactionRecurrence recurrence,
    short interval,
    String description,
    LocalDate nextTransactionDate
) implements Serializable {
    public static AutomaticTransactionResponseDTO fromEntity(
            AutomaticTransactionModel model
    ) {
        return new AutomaticTransactionResponseDTO(
                model.getId(),
                WalletSimpleResponseDTO.fromEntity(model.getWallet()),
                model.getName(),
                model.getWallet().getCurrency().format(model.getAmount()),
                model.getCategory(),
                model.getType(),
                model.getRecurrence(),
                model.getInterval(),
                model.getDescription(),
                model.getNextTransactionDate()
        );
    }
}
