package com.roadrescue.auth_service.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String profileImageUrl;
    private Boolean isVerified;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<VehicleDTO> vehicles;
    private MechanicProfileDTO mechanicProfile;
}
