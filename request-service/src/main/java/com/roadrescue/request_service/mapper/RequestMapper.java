package com.roadrescue.request_service.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.roadrescue.request_service.dto.BreakdownRequest;
import com.roadrescue.request_service.dto.BreakdownRequestEvent;
import com.roadrescue.request_service.dto.UserDTO;
import com.roadrescue.request_service.dto.VehicleDTO;
import com.roadrescue.request_service.model.IssueType;
import com.roadrescue.request_service.model.Request;
import com.roadrescue.request_service.model.RequestStatus;

@Component
public class RequestMapper {
    public Request toRequest(BreakdownRequest breakdownRequest, UserDTO userDTO) {
        return Request.builder()
                .userId(userDTO.getId())
                .vehicleIds(userDTO.getVehicles().stream().map(VehicleDTO::getId).toList())
                .locationLatitude(breakdownRequest.getCurrentLocationLat())
                .locationLongitude(breakdownRequest.getCurrentLocationLng())
                .issueType(IssueType.valueOf(breakdownRequest.getIssueType()))
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
}
