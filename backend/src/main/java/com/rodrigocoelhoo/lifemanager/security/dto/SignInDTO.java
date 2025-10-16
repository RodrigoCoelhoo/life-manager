package com.rodrigocoelhoo.lifemanager.security.dto;

import jakarta.validation.constraints.NotNull;

public record SignInDTO(
        @NotNull(message = "Username must be provided")
        String username,

        @NotNull(message = "Password must be provided")
        String password
) { }
