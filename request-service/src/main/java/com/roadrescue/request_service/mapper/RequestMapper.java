package com.roadrescue.request_service.mapper;

import java.time.LocalDateTime;

import com.roadrescue.request_service.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.roadrescue.request_service.model.IssueType;
import com.roadrescue.request_service.model.Request;
import com.roadrescue.request_service.model.RequestStatus;

@Component
@Slf4j
public class RequestMapper {
    public Request toRequest(BreakdownRequest breakdownRequest, UserDTO userDTO) {
        return Request.builder()
                .userId(userDTO.getId())
                .vehicleIds(userDTO.getVehicles().stream().map(VehicleDTO::getId).toList())
                .locationLatitude(breakdownRequest.getCurrentLocationLat())
                .locationLongitude(breakdownRequest.getCurrentLocationLng())
                .issueType(breakdownRequest.getIssueType())
                .description(breakdownRequest.getDescription())
                .address(breakdownRequest.getAddress())
                .status(RequestStatus.PENDING)
                .build();

    }

    public BreakdownRequestEvent toBreakdownRequestEvent(Request savedRequest, UserDTO userDTO) {
        return BreakdownRequestEvent.builder()
                .requestId(savedRequest.getId())
                .userId(userDTO.getId())
                .latitude(savedRequest.getLocationLatitude())
                .longitude(savedRequest.getLocationLongitude())
                .issueType(savedRequest.getIssueType())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public BreakdownRequestDTO convertToBreakdownRequestDTO(Request request, UserDTO mechanicDTO) {
        BreakdownRequestDTO dto = BreakdownRequestDTO.builder()
                .id(request.getId())
                .userId(request.getUserId())
                .vehicleIds(request.getVehicleIds())
                .locationLatitude(request.getLocationLatitude())
                .locationLongitude(request.getLocationLongitude())
                .address(request.getAddress())
                .issueType(request.getIssueType())
                .description(request.getDescription())
                .mechanicId(request.getMechanicId())
                .mechanicName(mechanicDTO.getFullName())
                .mechanicPhone(mechanicDTO.getPhone())
                .status(request.getStatus())
                .partsUsed(request.getPartsUsed())
                .laborCharge(request.getLaborCharge())
                .partsCharge(request.getPartsCharge())
                .finalAmount(request.getFinalAmount())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
        return dto;
    }
}
