package com.rodrigocoelhoo.lifemanager.users;

import com.rodrigocoelhoo.lifemanager.users.dto.UpdatePasswordDTO;
import com.rodrigocoelhoo.lifemanager.users.dto.UserDTO;
import com.rodrigocoelhoo.lifemanager.users.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile() {
        UserModel user = userService.getLoggedInUser();
        return ResponseEntity.ok(UserResponseDTO.fromEntity(user));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @RequestBody @Valid UserDTO data
    ) {
        UserModel user = userService.updateUser(data);
        return ResponseEntity.ok(UserResponseDTO.fromEntity(user));
    }

    @PutMapping("/update-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid UpdatePasswordDTO data
    ) {
        userService.updatePassword(data);
        return ResponseEntity.noContent().build();
    }
}

