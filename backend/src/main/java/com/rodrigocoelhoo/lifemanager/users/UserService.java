package com.rodrigocoelhoo.lifemanager.users;

import com.rodrigocoelhoo.lifemanager.exceptions.DuplicateFieldException;
import com.rodrigocoelhoo.lifemanager.exceptions.InvalidCredentialsException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.security.dto.SignUpDTO;
import com.rodrigocoelhoo.lifemanager.users.dto.UpdatePasswordDTO;
import com.rodrigocoelhoo.lifemanager.users.dto.UserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public UserModel createUser(SignUpDTO data) {
        if (userRepository.findByUsername(data.username()).isPresent())
            throw new DuplicateFieldException("That username is already taken. Please choose another.");

        if (userRepository.findByEmail(data.email()).isPresent())
            throw new DuplicateFieldException("An account with this email already exists. Try logging in instead.");

        String encryptedPassword = passwordEncoder.encode(data.password());
        UserModel newUser = new UserModel(
                data.username(),
                data.firstName(),
                data.lastName(),
                data.email(),
                encryptedPassword
        );

        return userRepository.save(newUser);
    }

    public UserModel updateUser(UserDTO data) {
        UserModel user = getLoggedInUser();

        boolean matches = passwordEncoder.matches(
                data.passwordConfirmation(),
                user.getPassword()
        );

        if (!matches) {
            throw new InvalidCredentialsException("Password confirmation is incorrect");
        }

        if (!user.getUsername().equals(data.username())) {
            if (userRepository.findByUsername(data.username()).isPresent())
                throw new DuplicateFieldException("That username is already taken. Please choose another.");
        }

        if (!user.getEmail().equals(data.email())) {
            if (userRepository.findByEmail(data.email()).isPresent())
                throw new DuplicateFieldException("An account with this email already exists. Try logging in instead.");
        }

        user.setUsername(data.username());
        user.setFirstName(data.firstName());
        user.setLastName(data.lastName());
        user.setEmail(data.email());

        return userRepository.save(user);
    }

    public void updatePassword(UpdatePasswordDTO data) {
        UserModel user = getLoggedInUser();

        boolean matches = passwordEncoder.matches(
                data.passwordConfirmation(),
                user.getPassword()
        );

        if (!matches) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        if (passwordEncoder.matches(data.newPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("New password must be different from the current password");
        }

        String encryptedPassword = passwordEncoder.encode(data.newPassword());
        user.setPassword(encryptedPassword);

        userRepository.save(user);
    }

    public UserModel getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("User with ID '" + id + "' not found"));
    }

    public UserModel getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFound("User with Username '" + username + "' not found"));
    }

    public UserModel getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
    }
}
