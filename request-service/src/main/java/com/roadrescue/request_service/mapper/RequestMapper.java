package com.roadrescue.request_service.mapper;

import java.time.LocalDateTime;

import com.roadrescue.request_service.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.roadrescue.request_service.model.Request;
import com.roadrescue.request_service.model.RequestStatus;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class RequestMapper {
    public Request toRequest(BreakdownRequest breakdownRequest, UserDTO userDTO) {
        List<String> photoUrls = breakdownRequest.getPhotoUrls() == null
                ? List.of()
                : breakdownRequest.getPhotoUrls().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();

        return Request.builder()
                .userId(userDTO.getId())
                .selectedVehicleId(breakdownRequest.getVehicleId())
                .vehicleIds(List.of(breakdownRequest.getVehicleId()))
                .locationLatitude(breakdownRequest.getCurrentLocationLat())
                .locationLongitude(breakdownRequest.getCurrentLocationLng())
                .issueType(breakdownRequest.getIssueType())
                .description(breakdownRequest.getDescription())
                .address(breakdownRequest.getAddress())
                .status(RequestStatus.PENDING)
                .excludedMechanicIds(List.of())
                .photoUrls(photoUrls)
                .build();

    }

    public BreakdownRequestEvent toBreakdownRequestEvent(Request savedRequest, UserDTO userDTO) {
        return BreakdownRequestEvent.builder()
                .requestId(savedRequest.getId())
                .userId(userDTO.getId())
                .latitude(savedRequest.getLocationLatitude())
                .longitude(savedRequest.getLocationLongitude())
                .issueType(savedRequest.getIssueType())
                .excludedMechanicIds(savedRequest.getExcludedMechanicIds())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public BreakdownRequestDTO convertToBreakdownRequestDTO(Request request, UserDTO mechanicDTO) {
        BreakdownRequestDTO dto = BreakdownRequestDTO.builder()
                .id(request.getId())
                .userId(request.getUserId())
                .selectedVehicleId(request.getSelectedVehicleId())
                .vehicleIds(request.getVehicleIds())
                .locationLatitude(request.getLocationLatitude())
                .locationLongitude(request.getLocationLongitude())
                .address(request.getAddress())
                .issueType(request.getIssueType())
                .description(request.getDescription())
                .photoUrls(request.getPhotoUrls())
                .mechanicId(request.getMechanicId())
                .status(request.getStatus())
                .partsUsed(request.getPartsUsed())
                .laborCharge(request.getLaborCharge())
                .partsCharge(request.getPartsCharge())
                .finalAmount(request.getFinalAmount())
                .serviceNotes(request.getServiceNotes())
                .beforeServicePhotos(request.getBeforeServicePhotos())
                .afterServicePhotos(request.getAfterServicePhotos())
                .serviceStartedAt(request.getServiceStartedAt())
                .completedAt(request.getCompletedAt())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();

        if (mechanicDTO != null) {
            dto.setMechanicName(mechanicDTO.getFullName());
            dto.setMechanicPhone(mechanicDTO.getPhone());
            dto.setMechanicProfileImageUrl(mechanicDTO.getProfileImageUrl());
            dto.setMechanicRating(
                    mechanicDTO.getMechanicProfile() != null
                            ? mechanicDTO.getMechanicProfile().getRating()
                            : null
            );
        }

        return dto;
    }
}
