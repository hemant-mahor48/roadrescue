package com.roadrescue.rating_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String role;
}
