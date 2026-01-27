package com.roadrescue.matching_service.service;

import com.roadrescue.matching_service.dto.BreakdownRequestEvent;
import com.roadrescue.matching_service.dto.NearbyMechanic;

import java.util.UUID;

public interface MatchingService {
    UUID findBestMechanic(BreakdownRequestEvent event);
    Double calculateMatchScore(NearbyMechanic mechanic, BreakdownRequestEvent event);
}
