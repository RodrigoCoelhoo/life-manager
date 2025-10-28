package com.rodrigocoelhoo.lifemanager.finances.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionDTO(

        @NotNull(message = "Wallet ID must be provided")
        Long walletId,

        @NotNull(message = "Amount must be provided")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotNull(message = "Description can be blank, but can't be null")
        String description,

        @NotNull(message = "Date must be provided")
        LocalDate date,

        @NotBlank(message ="Type must be provided")
        String category
) { }
