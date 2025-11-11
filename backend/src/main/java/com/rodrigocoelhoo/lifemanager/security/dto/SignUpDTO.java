package com.rodrigocoelhoo.lifemanager.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpDTO(

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

    @NotBlank
    @Size(min = 8, max = 100, message = "Password must have between 8 and 100 characters.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&\\-_])[A-Za-z\\d@$!%*?&\\-_]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&-_)"
    )
    String password
) { }
