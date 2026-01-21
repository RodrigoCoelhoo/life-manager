package com.rodrigocoelhoo.lifemanager.finances.dto;

import com.rodrigocoelhoo.lifemanager.finances.model.AutomaticTransactionModel;
import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseCategory;
import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseType;

import java.io.Serializable;
import java.time.LocalDate;

public record AutomaticTransactionSimple(
        Long id,
        String walletName,
        String name,
        String amount,
        ExpenseCategory category,
        ExpenseType type,
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
                model.getCategory(),
                model.getType(),
                model.getNextTransactionDate()
        );
    }
}