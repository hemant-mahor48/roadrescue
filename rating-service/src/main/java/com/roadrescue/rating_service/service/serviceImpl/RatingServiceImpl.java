package com.roadrescue.rating_service.service.serviceImpl;

import com.roadrescue.rating_service.client.RequestFeignClient;
import com.roadrescue.rating_service.client.UserFeignClient;
import com.roadrescue.rating_service.dto.RatingDTO;
import com.roadrescue.rating_service.dto.RatingRequest;
import com.roadrescue.rating_service.dto.RequestSummaryDTO;
import com.roadrescue.rating_service.dto.ReviewEvent;
import com.roadrescue.rating_service.dto.UserDTO;
import com.roadrescue.rating_service.model.Rating;
import com.roadrescue.rating_service.repository.RatingRepository;
import com.roadrescue.rating_service.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserFeignClient userFeignClient;
    private final RequestFeignClient requestFeignClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.reviews-topic}")
    private String reviewsTopic;

    @Override
    @Transactional
    public RatingDTO submitRating(String email, RatingRequest request) {
        UserDTO currentUser = userFeignClient.getCurrentUser(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getData();

        RequestSummaryDTO requestSummary = requestFeignClient.getRequestById(request.getRequestId(), email)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"))
                .getData();

        validateRatingSubmission(currentUser, requestSummary, request);

        Rating rating = ratingRepository.findByRequestId(request.getRequestId())
                .orElseGet(Rating::new);

        if (rating.getCustomerId() != null && !Objects.equals(rating.getCustomerId(), currentUser.getId())) {
            throw new IllegalArgumentException("This request has already been rated by another customer");
        }

        rating.setRequestId(request.getRequestId());
        rating.setCustomerId(currentUser.getId());
        rating.setMechanicId(requestSummary.getMechanicId());
        rating.setScore(request.getScore());
        rating.setReview(request.getReview() == null ? null : request.getReview().trim());

        Rating savedRating = ratingRepository.save(rating);
        Double newAverageRating = ratingRepository.calculateAverageScoreByMechanicId(savedRating.getMechanicId());
        int totalReviews = Math.toIntExact(ratingRepository.countByMechanicId(savedRating.getMechanicId()));

        kafkaTemplate.send(reviewsTopic, savedRating.getMechanicId().toString(), ReviewEvent.builder()
                .mechanicId(savedRating.getMechanicId())
                .requestId(savedRating.getRequestId())
                .customerId(savedRating.getCustomerId())
                .rating(savedRating.getScore())
                .review(savedRating.getReview())
                .newAvgRating(roundToTwoDecimals(newAverageRating))
                .totalReviews(totalReviews)
                .createdAt(LocalDateTime.now())
                .build());

        return toDto(savedRating);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingDTO getRatingByRequestId(UUID requestId) {
        return ratingRepository.findByRequestId(requestId)
                .map(this::toDto)
                .orElse(null);
    }

    private RatingDTO toDto(Rating rating) {
        return RatingDTO.builder()
                .id(rating.getId())
                .requestId(rating.getRequestId())
                .customerId(rating.getCustomerId())
                .mechanicId(rating.getMechanicId())
                .score(rating.getScore())
                .review(rating.getReview())
                .createdAt(rating.getCreatedAt())
                .build();
    }

    private void validateRatingSubmission(UserDTO currentUser, RequestSummaryDTO requestSummary, RatingRequest request) {
        if (requestSummary == null) {
            throw new IllegalArgumentException("Request not found");
        }
        if (!Objects.equals(requestSummary.getUserId(), currentUser.getId())) {
            throw new IllegalArgumentException("You can only rate your own paid request");
        }
        if (!"PAID".equalsIgnoreCase(requestSummary.getStatus())) {
            throw new IllegalArgumentException("Rating is allowed only after payment is completed");
        }
        if (requestSummary.getMechanicId() == null) {
            throw new IllegalArgumentException("No mechanic was assigned to this request");
        }
        if (!Objects.equals(requestSummary.getMechanicId(), request.getMechanicId())) {
            throw new IllegalArgumentException("Mechanic does not match the paid request");
        }
    }

    private double roundToTwoDecimals(Double value) {
        double safeValue = value == null ? 0.0 : value;
        return Math.round(safeValue * 100.0) / 100.0;
    }
}
