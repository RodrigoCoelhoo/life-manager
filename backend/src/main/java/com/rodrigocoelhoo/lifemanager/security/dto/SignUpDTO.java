package com.rodrigocoelhoo.lifemanager.security.dto;

import com.rodrigocoelhoo.lifemanager.users.UserRole;

public record SignUpDTO(String username, String password, UserRole role) {
}
