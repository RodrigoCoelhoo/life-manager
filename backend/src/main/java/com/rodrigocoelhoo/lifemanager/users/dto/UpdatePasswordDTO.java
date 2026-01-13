package com.rodrigocoelhoo.lifemanager.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePasswordDTO(
        @NotBlank
        @Size(min = 8, max = 100, message = "Password must have between 8 and 100 characters.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&\\-_])[A-Za-z\\d@$!%*?&\\-_]+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&-_)"
        )
        String newPassword,

        @NotBlank(message = "Password confirmation is required")
        String passwordConfirmation
) { }
