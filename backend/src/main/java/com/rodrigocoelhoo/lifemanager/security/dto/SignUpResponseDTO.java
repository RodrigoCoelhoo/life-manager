package com.rodrigocoelhoo.lifemanager.security.dto;

import com.rodrigocoelhoo.lifemanager.users.UserModel;

public record SignUpResponseDTO(String id, String username, String first_name, String last_name, String email) {

    public static SignUpResponseDTO fromEntity(UserModel user) {
        return new SignUpResponseDTO(
                user.getId().toString(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

}
