package com.roadrescue.auth_service.service.serviceImpl;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class MechanicServiceImpl implements MechanicService {

    private final UserRepository userRepository;
    private final MechanicProfileRepository mechanicProfileRepository;
    private final ModelMapper modelMapper;
    private final AadhaarValidator aadhaarValidator;
    private final DrivingLicenseValidator drivingLicenseValidator;

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
        profile.setVehicleNumber(request.getVehicleNumber());
        profile.setLicenseNumber(request.getLicenseNumber());
        profile.setCurrentLocationLat(request.getCurrentLocationLat());
        profile.setCurrentLocationLng(request.getCurrentLocationLng());
        profile.setIsAvailable(false); // Not available until verified

        user.setRole(UserRole.MECHANIC);

        userRepository.save(user);
        MechanicProfile saved = mechanicProfileRepository.save(profile);

        log.info("Mechanic registered: userEmail={}, profileId={}", email, saved.getId());

        return modelMapper.map(saved, MechanicProfileDTO.class);
    }

    @Override
    @Transactional
    public MechanicProfileDTO submitVerification(String email, MechanicVerificationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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
        profile.setAadharVerified(true);
        profile.setPoliceVerificationDone(true);

        user.setProfileImageUrl(request.getProfileImageUrl());
        user.setIsActive(true);
        user.setIsVerified(true);

        userRepository.save(user);
        MechanicProfile saved = mechanicProfileRepository.save(profile);
        log.info("Mechanic verified: userEmail={}, profileId={}", email, saved.getId());
        return modelMapper.map(saved, MechanicProfileDTO.class);
    }
}
