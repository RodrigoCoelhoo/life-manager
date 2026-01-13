package com.rodrigocoelhoo.lifemanager.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters long")
        @Pattern(
                regexp = "^[A-Za-z0-9_-]+$",
                message = "Username can only contain letters, numbers, underscores, and hyphens"
        )
        String username,

        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters long")
        @Pattern(
                regexp = "^\\p{L}+$",
                message = "First name can only contain letters"
        )
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters long")
        @Pattern(
                regexp = "^\\p{L}+$",
                message = "Last name can only contain letters"
        )
        String lastName,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password confirmation is required")
        String passwordConfirmation
) {}
