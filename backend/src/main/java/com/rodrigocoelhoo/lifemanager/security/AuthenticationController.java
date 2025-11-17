package com.rodrigocoelhoo.lifemanager.security;

import com.rodrigocoelhoo.lifemanager.security.dto.SignInDTO;
import com.rodrigocoelhoo.lifemanager.security.dto.SignInResponseDTO;
import com.rodrigocoelhoo.lifemanager.security.dto.SignUpDTO;
import com.rodrigocoelhoo.lifemanager.security.dto.SignUpResponseDTO;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

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
        UserModel user = userService.createUser(data);
        return ResponseEntity.status(201).body(SignUpResponseDTO.fromEntity(user));
    }
}
