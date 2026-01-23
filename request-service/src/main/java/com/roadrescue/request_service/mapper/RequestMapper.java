package com.roadrescue.request_service.mapper;

import com.roadrescue.request_service.dto.BreakdownRequest;
import com.roadrescue.request_service.dto.BreakdownRequestEvent;
import com.roadrescue.request_service.dto.UserDTO;
import com.roadrescue.request_service.dto.VehicleDTO;
import com.roadrescue.request_service.model.IssueType;
import com.roadrescue.request_service.model.Request;
import com.roadrescue.request_service.model.RequestStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
        Map<BigDecimal, BigDecimal> locationMap = new HashMap<>();
        locationMap.put(savedRequest.getLocationLatitude(), savedRequest.getLocationLongitude());
        return BreakdownRequestEvent.builder()
                .requestId(savedRequest.getId())
                .userId(userDTO.getId())
                .location(locationMap)
                .issueType(savedRequest.getIssueType())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
