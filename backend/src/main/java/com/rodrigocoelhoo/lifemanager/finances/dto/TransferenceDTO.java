package com.rodrigocoelhoo.lifemanager.finances.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferenceDTO(
        @NotNull(message = "From Wallet ID must be provided")
        Long fromWalletId,

        @NotNull(message = "To Wallet ID must be provided")
        Long toWalletId,

        @Positive(message = "Amount must be positive")
        @NotNull(message = "Amount must be provided")
        BigDecimal amount,

        @NotNull(message = "Date must be provided")
        LocalDate date,

        @NotNull(message = "Description can be blank but not null")
        @Size(max = 512, message = "Description has a limit of 512 characters")
        String description
) { }
