package com.roadrescue.request_service.dto;

import com.roadrescue.request_service.model.IssueType;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Validated
public class BreakdownRequest {

    @NotNull(message = "Vehicle is required")
    private UUID vehicleId;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    private BigDecimal currentLocationLat;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    private BigDecimal currentLocationLng;

    @NotNull(message = "Issue type is required")
    private IssueType issueType;

    private String description;

    private String address;

    @Size(max = 5, message = "You can attach up to 5 photos")
    private List<String> photoUrls;
}
