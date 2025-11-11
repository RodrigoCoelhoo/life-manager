package com.rodrigocoelhoo.lifemanager.users;

import com.rodrigocoelhoo.lifemanager.exceptions.DuplicateFieldException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.security.dto.SignUpDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserModel createUser(SignUpDTO data) {
        if (userRepository.findByUsername(data.username()).isPresent())
            throw new DuplicateFieldException("That username is already taken. Please choose another.");

        if (userRepository.findByEmail(data.email()).isPresent())
            throw new DuplicateFieldException("An account with this email already exists. Try logging in instead.");

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        UserModel newUser = new UserModel(
                data.username(),
                data.firstName(),
                data.lastName(),
                data.email(),
                encryptedPassword
        );

        return userRepository.save(newUser);
    }

    public UserModel getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("User with ID '" + id + "' not found"));
    }

    public UserModel getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
    }
}
