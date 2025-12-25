package com.demo.securityjwt.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.securityjwt.dto.AuthResponse;
import com.demo.securityjwt.dto.LoginRequest;
import com.demo.securityjwt.dto.RegisterRequest;
import com.demo.securityjwt.entity.User;
import com.demo.securityjwt.entity.enums.Role;
import com.demo.securityjwt.repository.UserRepository;
import com.demo.securityjwt.security.jwt.JwtService;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository repository,
            PasswordEncoder encoder,
            AuthenticationManager authManager,
            JwtService jwtService) {
        this.repository = repository;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest req) {

        if (repository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already exists");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(Role.ROLE_USER);

        repository.save(user);
    }

    public AuthResponse login(LoginRequest req) {

        // validasi credential (Spring Security)
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()));

        User user = repository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}
