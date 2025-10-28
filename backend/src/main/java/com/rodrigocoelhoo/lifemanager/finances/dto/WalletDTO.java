package com.rodrigocoelhoo.lifemanager.finances.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record WalletDTO(

        @NotBlank(message = "Wallet name cannot be blank")
        @Size(max = 50, message = "Name cannot exceed 50 characters")
        String name,

        @NotBlank(message = "Wallet type must be provided")
        @Size(max = 15, message = "Name cannot exceed 15 characters")
        String type,

        @NotNull(message = "Wallet balance must be provided")
        @PositiveOrZero(message = "Wallet balance can't be negative")
        BigDecimal balance,

        @NotBlank(message = "Wallet currency must be provided")
        String currency
) {
}
