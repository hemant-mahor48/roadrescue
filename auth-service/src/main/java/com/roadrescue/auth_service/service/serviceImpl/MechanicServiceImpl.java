package com.roadrescue.auth_service.service.serviceImpl;

import com.roadrescue.auth_service.client.LocationServiceClient;
import com.roadrescue.auth_service.dto.MechanicProfileDTO;
import com.roadrescue.auth_service.dto.MechanicRegistrationRequest;
import com.roadrescue.auth_service.dto.MechanicVerificationRequest;
import com.roadrescue.auth_service.exceptions.BusinessException;
import com.roadrescue.auth_service.exceptions.ResourceNotFoundException;
import com.roadrescue.auth_service.model.MechanicProfile;
import com.roadrescue.auth_service.model.User;
import com.roadrescue.auth_service.model.UserRole;
import com.roadrescue.auth_service.repository.MechanicProfileRepository;
import com.roadrescue.auth_service.repository.UserRepository;
import com.roadrescue.auth_service.service.MechanicService;
import com.roadrescue.auth_service.util.AadhaarValidator;
import com.roadrescue.auth_service.util.DrivingLicenseValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MechanicServiceImpl implements MechanicService {

    private final UserRepository userRepository;
    private final MechanicProfileRepository mechanicProfileRepository;
    private final ModelMapper modelMapper;
    private final AadhaarValidator aadhaarValidator;
    private final DrivingLicenseValidator drivingLicenseValidator;
    private final LocationServiceClient locationServiceClient;

    @Override
    @Transactional
    public MechanicProfileDTO registerMechanic(String email, MechanicRegistrationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (mechanicProfileRepository.existsByUserId(user.getId())) {
            throw new BusinessException("User already registered as mechanic");
        }

        MechanicProfile profile = new MechanicProfile();
        profile.setUser(user);
        profile.setCurrentLocationLat(request.getCurrentLocationLat());
        profile.setCurrentLocationLng(request.getCurrentLocationLng());
        profile.setIsAvailable(false); // Not available until verified

        user.setRole(UserRole.MECHANIC);

        userRepository.save(user);
        com.roadrescue.auth_service.model.MechanicProfile saved = mechanicProfileRepository.save(profile);

        //Updating current location in Redis
        locationServiceClient.updateLocation(profile.getId(), profile.getCurrentLocationLat(), profile.getCurrentLocationLng());

        log.info("Mechanic registered: userEmail={}, profileId={}", email, saved.getId());

        return modelMapper.map(saved, MechanicProfileDTO.class);
    }

    @Override
    @Transactional
    public MechanicProfileDTO submitVerification(String email, MechanicVerificationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        //Duplicate verification
        if(mechanicProfileRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
            throw new BusinessException("Aadhaar Number already in use");
        }

        if(mechanicProfileRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new BusinessException("Driving License Number already in use");
        }

        //verification
        if(!AadhaarValidator.isValidAadhaar(request.getAadhaarNumber())) {
            throw new BusinessException("Invalid Aadhaar Number");
        }
        if(!DrivingLicenseValidator.isValidDL(request.getLicenseNumber())) {
            throw new BusinessException("Invalid Driving License Number");
        }

        MechanicProfile profile = mechanicProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic profile not found"));
        profile.setIsAvailable(true);
        profile.setAadhaarVerified(true);
        profile.setAadhaarNumber(request.getAadhaarNumber());
        profile.setLicenseNumber(request.getLicenseNumber());
        profile.setPoliceVerificationDone(true);

        user.setProfileImageUrl(request.getProfileImageUrl());
        user.setIsActive(true);
        user.setIsVerified(true);

        userRepository.save(user);
        MechanicProfile saved = mechanicProfileRepository.save(profile);
        log.info("Mechanic verified: userEmail={}, profileId={}", email, saved.getId());

        //Updating availability in Redis
        locationServiceClient.updateAvailability(profile.getId(), saved.getIsAvailable());

        return modelMapper.map(saved, MechanicProfileDTO.class);
    }

    @Transactional
    @Override
    public void updateLocation(String email, BigDecimal lat, BigDecimal lng) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        com.roadrescue.auth_service.model.MechanicProfile profile = mechanicProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic profile not found"));

        // Update in PostgreSQL
        profile.setCurrentLocationLat(lat);
        profile.setCurrentLocationLng(lng);
        mechanicProfileRepository.save(profile);

        // ✅ Sync to Redis (for geospatial queries)
        locationServiceClient.updateLocation(profile.getId(), lat, lng);

        log.info("Updated location for mechanic: {} to ({}, {})",
                profile.getId(), lat, lng);
    }

    @Override
    @Transactional
    public void updateAvailability(String email, Boolean available) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        com.roadrescue.auth_service.model.MechanicProfile profile = mechanicProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic profile not found"));

        // Update in PostgreSQL
        profile.setIsAvailable(available);
        mechanicProfileRepository.save(profile);

        // ✅ Sync to Redis
        locationServiceClient.updateAvailability(profile.getId(), available);

        log.info("Updated availability for mechanic: {} to {}",
                profile.getId(), available);
    }

    @Override
    public MechanicProfileDTO getMechanicProfile(UUID mechanicId) {
        com.roadrescue.auth_service.model.MechanicProfile profile = mechanicProfileRepository.findById(mechanicId)
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic profile not found"));
        return modelMapper.map(profile, MechanicProfileDTO.class);
    }
}
