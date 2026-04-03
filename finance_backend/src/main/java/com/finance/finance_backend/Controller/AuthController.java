package com.finance.finance_backend.Controller;

import com.finance.finance_backend.Dto.AuthResponse;
import com.finance.finance_backend.Dto.LoginRequest;
import com.finance.finance_backend.Model.User;
import com.finance.finance_backend.Repository.UserRepository;
import com.finance.finance_backend.Security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getEmail(), req.getPassword()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401)
                    .body("Invalid email or password");
        }

        User user = userRepository.findByEmail(req.getEmail()).orElseThrow();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .name(user.getName())
                .build());
    }
}