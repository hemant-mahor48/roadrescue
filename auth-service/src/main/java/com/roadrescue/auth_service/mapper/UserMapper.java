package com.roadrescue.auth_service.mapper;

import com.roadrescue.auth_service.dto.RegisterRequest;
import com.roadrescue.auth_service.model.User;
import com.roadrescue.auth_service.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toUser(RegisterRequest request) {
        if(request == null){
            return null;
        }
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.valueOf(request.getRole()))
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .build();
    }
}
