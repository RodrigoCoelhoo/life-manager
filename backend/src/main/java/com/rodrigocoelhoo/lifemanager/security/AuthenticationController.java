package com.rodrigocoelhoo.lifemanager.security;

import com.rodrigocoelhoo.lifemanager.exceptions.DuplicateFieldException;
import com.rodrigocoelhoo.lifemanager.security.dto.SignInDTO;
import com.rodrigocoelhoo.lifemanager.security.dto.SignInResponseDTO;
import com.rodrigocoelhoo.lifemanager.security.dto.SignUpDTO;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody @Valid SignInDTO data) {
        var UsernamePasswordToken = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var auth = this.authenticationManager.authenticate(UsernamePasswordToken);

        var token = tokenService.generateToken((UserModel) auth.getPrincipal());

        return ResponseEntity.ok(new SignInResponseDTO(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignUpDTO data) {
        if (userRepository.findByUsername(data.username()) != null)
            throw new DuplicateFieldException("username", data.username());

        if (userRepository.findByEmail(data.email()) != null)
            throw new DuplicateFieldException("email", data.email());

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        UserModel newUser = new UserModel(
                data.username(),
                data.firstName(),
                data.lastName(),
                data.email(),
                encryptedPassword
        );

        this.userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }
}
