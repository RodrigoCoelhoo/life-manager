package com.rodrigocoelhoo.lifemanager.finances.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WalletUpdateDTO(
        @NotBlank(message = "Wallet name cannot be blank")
        @Size(max = 50, message = "Name cannot exceed 50 characters")
        String name,

        @NotBlank(message = "Wallet type must be provided")
        @Size(max = 15, message = "Name cannot exceed 15 characters")
        String type
) {
}
