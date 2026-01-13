package com.rodrigocoelhoo.lifemanager.users.dto;

import com.rodrigocoelhoo.lifemanager.users.UserModel;

public record UserResponseDTO(
        String username,
        String firstName,
        String lastName,
        String email
) {
    public static UserResponseDTO fromEntity(UserModel user) {
        return new UserResponseDTO(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }
}
