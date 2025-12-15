package com.rodrigocoelhoo.lifemanager.finances.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AutomaticTransactionDTO(
        @NotNull(message = "Wallet ID is required")
        Long walletId,

        @NotBlank(message = "Name cannot be blank")
        @Size(max = 50, message = "Name cannot exceed 50 characters")
        String name,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Category is required")
        String category,

        @NotBlank(message = "Recurrence is required")
        String recurrence,

        @Positive(message = "Interval must be greater than zero")
        short interval,

        @NotNull(message = "Description is required")
        @Size(max = 512, message = "Description cannot exceed 512 characters")
        String description,

        @NotNull(message = "Next transaction date is required")
        LocalDate nextTransactionDate
) { }
