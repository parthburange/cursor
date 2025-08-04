package com.skillshare.service;

import com.skillshare.dto.AuthResponse;
import com.skillshare.dto.LoginRequest;
import com.skillshare.dto.RegisterRequest;
import com.skillshare.entity.Role;
import com.skillshare.entity.User;
import com.skillshare.repository.UserRepository;
import com.skillshare.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        
        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        
        return new AuthResponse(token, "Bearer", savedUser.getUsername(), savedUser.getRole().name());
    }
    
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = jwtService.generateToken(user);
        
        return new AuthResponse(token, "Bearer", user.getUsername(), user.getRole().name());
    }
}