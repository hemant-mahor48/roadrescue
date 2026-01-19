package com.roadrescue.auth_service.service.serviceImpl;

import com.roadrescue.auth_service.dto.AuthResponse;
import com.roadrescue.auth_service.dto.LoginRequest;
import com.roadrescue.auth_service.dto.RegisterRequest;
import com.roadrescue.auth_service.dto.UserDTO;
import com.roadrescue.auth_service.mapper.UserMapper;
import com.roadrescue.auth_service.model.User;
import com.roadrescue.auth_service.repository.UserRepository;
import com.roadrescue.auth_service.service.AuthService;
import com.roadrescue.auth_service.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    @Override
    public UUID registerUser(RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = userMapper.toUser(request);
        return repository.save(user).getId();
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) throws InvalidCredentialsException {
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        repository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), String.valueOf(user.getRole()));

        return new AuthResponse(token, modelMapper.map(user, UserDTO.class));
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e){
            return false;
        }
    }
}
