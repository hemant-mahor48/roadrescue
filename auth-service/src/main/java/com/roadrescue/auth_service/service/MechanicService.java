package com.roadrescue.auth_service.service;

import com.roadrescue.auth_service.dto.MechanicProfileDTO;
import com.roadrescue.auth_service.dto.MechanicRegistrationRequest;
import com.roadrescue.auth_service.dto.MechanicVerificationRequest;

import java.math.BigDecimal;
import java.util.UUID;

public interface MechanicService {
    MechanicProfileDTO registerMechanic(String email, MechanicRegistrationRequest request);

    MechanicProfileDTO submitVerification(String email, MechanicVerificationRequest request);

    void updateLocation(String email, BigDecimal lat, BigDecimal lng);

    void updateAvailability(String email, Boolean available);

    MechanicProfileDTO getMechanicProfile(UUID mechanicId);
}
