package com.roadrescue.rating_service.service;

import com.roadrescue.rating_service.dto.RatingDTO;
import com.roadrescue.rating_service.dto.RatingRequest;

import java.util.UUID;

public interface RatingService {
    RatingDTO submitRating(String email, RatingRequest request);
    RatingDTO getRatingByRequestId(UUID requestId);
}
