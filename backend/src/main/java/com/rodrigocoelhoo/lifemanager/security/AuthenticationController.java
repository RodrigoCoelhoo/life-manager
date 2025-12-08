package com.rodrigocoelhoo.lifemanager.security;

import com.rodrigocoelhoo.lifemanager.security.dto.*;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
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

        UserModel user = (UserModel) auth.getPrincipal();

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        // HttpOnly refresh cookie (JavaScript can't read it, security against XSS)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // true https (production) | false http (development)
                .path("/api/auth")
                .maxAge(30 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(new AccessTokenResponseDTO(accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401).body("Refresh token missing");
        }

        String username = tokenService.validateRefreshToken(refreshToken);
        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        UserModel user = userService.getUser(username);
        String newAccessToken = tokenService.generateAccessToken(user);

        return ResponseEntity.ok(new AccessTokenResponseDTO(newAccessToken));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignUpDTO data) {
        UserModel user = userService.createUser(data);
        return ResponseEntity.status(201).body(SignUpResponseDTO.fromEntity(user));
    }
}
