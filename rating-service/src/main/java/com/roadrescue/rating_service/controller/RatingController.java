package com.roadrescue.rating_service.controller;

import com.roadrescue.rating_service.dto.ApiResponse;
import com.roadrescue.rating_service.dto.RatingDTO;
import com.roadrescue.rating_service.dto.RatingRequest;
import com.roadrescue.rating_service.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<ApiResponse<RatingDTO>> submitRating(
            @RequestHeader("X-auth-user") String email,
            @RequestBody @Valid RatingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Rating submitted successfully",
                ratingService.submitRating(email, request)
        ));
    }

    @GetMapping("/requests/{requestId}")
    public ResponseEntity<ApiResponse<RatingDTO>> getByRequestId(@PathVariable UUID requestId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Rating fetched successfully",
                ratingService.getRatingByRequestId(requestId)
        ));
    }
}
