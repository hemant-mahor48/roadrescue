package com.roadrescue.analytics_service.service.serviceImpl;

import com.roadrescue.analytics_service.dto.BreakdownRequestEvent;
import com.roadrescue.analytics_service.dto.MechanicAssignmentEvent;
import com.roadrescue.analytics_service.dto.PaymentEvent;
import com.roadrescue.analytics_service.dto.ReviewEvent;
import com.roadrescue.analytics_service.dto.ServiceCompletionEvent;
import com.roadrescue.analytics_service.model.BreakdownRequestAnalytics;
import com.roadrescue.analytics_service.model.MechanicAssignmentAnalytics;
import com.roadrescue.analytics_service.model.MechanicBadgeAnalytics;
import com.roadrescue.analytics_service.model.PaymentAnalytics;
import com.roadrescue.analytics_service.model.ReviewAnalytics;
import com.roadrescue.analytics_service.model.ServiceCompletionAnalytics;
import com.roadrescue.analytics_service.repository.BreakdownRequestAnalyticsRepository;
import com.roadrescue.analytics_service.repository.MechanicAssignmentAnalyticsRepository;
import com.roadrescue.analytics_service.repository.MechanicBadgeAnalyticsRepository;
import com.roadrescue.analytics_service.repository.PaymentAnalyticsRepository;
import com.roadrescue.analytics_service.repository.ReviewAnalyticsRepository;
import com.roadrescue.analytics_service.repository.ServiceCompletionAnalyticsRepository;
import com.roadrescue.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final BreakdownRequestAnalyticsRepository breakdownRequestRepository;
    private final MechanicAssignmentAnalyticsRepository mechanicAssignmentRepository;
    private final ServiceCompletionAnalyticsRepository serviceCompletionRepository;
    private final PaymentAnalyticsRepository paymentAnalyticsRepository;
    private final ReviewAnalyticsRepository reviewAnalyticsRepository;
    private final MechanicBadgeAnalyticsRepository mechanicBadgeAnalyticsRepository;

    @Override
    public void recordBreakdownRequest(BreakdownRequestEvent event) {
        breakdownRequestRepository.save(BreakdownRequestAnalytics.builder()
                .requestId(event.getRequestId())
                .customerId(event.getUserId())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .issueType(event.getIssueType())
                .requestedAt(event.getTimestamp())
                .build());
    }

    @Override
    public void recordMechanicAssignment(MechanicAssignmentEvent event) {
        LocalDateTime requestedAt = breakdownRequestRepository.findByRequestId(event.getRequestId())
                .map(BreakdownRequestAnalytics::getRequestedAt)
                .orElse(null);

        double responseTimeMins = requestedAt == null || event.getAssignedAt() == null
                ? 0.0
                : Math.max(0.0, ChronoUnit.SECONDS.between(requestedAt, event.getAssignedAt()) / 60.0);

        mechanicAssignmentRepository.findByRequestId(event.getRequestId())
                .ifPresent(existing -> mechanicAssignmentRepository.deleteById(existing.getId()));

        mechanicAssignmentRepository.save(MechanicAssignmentAnalytics.builder()
                .requestId(event.getRequestId())
                .mechanicId(event.getMechanicId())
                .customerId(event.getCustomerId())
                .estimatedAmount(event.getEstimatedAmount())
                .depositHoldAmount(event.getDepositHoldAmount())
                .responseTimeMins(responseTimeMins)
                .assignedAt(event.getAssignedAt())
                .build());

        recalculateBadges(event.getMechanicId(), null, null);
    }

    @Override
    public void recordServiceCompletion(ServiceCompletionEvent event) {
        serviceCompletionRepository.save(ServiceCompletionAnalytics.builder()
                .requestId(event.getRequestId())
                .customerId(event.getCustomerId())
                .mechanicId(event.getMechanicId())
                .serviceDurationMins(event.getServiceDurationMins())
                .partsUsed(event.getPartsUsed())
                .laborCharge(event.getLaborCharge())
                .partsCharge(event.getPartsCharge())
                .totalAmount(event.getTotalAmount())
                .completedAt(event.getCompletedAt())
                .build());
        recalculateBadges(event.getMechanicId(), null, null);
    }

    @Override
    public void recordPayment(PaymentEvent event) {
        paymentAnalyticsRepository.save(PaymentAnalytics.builder()
                .requestId(event.getRequestId())
                .paymentId(event.getPaymentId())
                .customerId(event.getCustomerId())
                .mechanicId(event.getMechanicId())
                .amount(event.getAmount())
                .mechanicEarning(event.getMechanicEarning())
                .platformFee(event.getPlatformFee())
                .status(event.getStatus())
                .paidAt(event.getPaidAt())
                .build());
    }

    @Override
    public void recordReview(ReviewEvent event) {
        reviewAnalyticsRepository.save(ReviewAnalytics.builder()
                .requestId(event.getRequestId())
                .mechanicId(event.getMechanicId())
                .customerId(event.getCustomerId())
                .rating(event.getRating())
                .review(event.getReview())
                .newAverageRating(event.getNewAvgRating())
                .totalReviews(event.getTotalReviews())
                .createdAt(event.getCreatedAt())
                .build());

        recalculateBadges(event.getMechanicId(), event.getNewAvgRating(), event.getTotalReviews());
    }

    @Override
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("breakdownRequests", breakdownRequestRepository.countBy());
        summary.put("mechanicAssignments", mechanicAssignmentRepository.count());
        summary.put("tyrePunctureRequests", breakdownRequestRepository.countByIssueType("TYRE_PUNCTURE"));
        summary.put("completedServices", serviceCompletionRepository.countBy());
        summary.put("successfulPayments", paymentAnalyticsRepository.countByStatus("SUCCESS"));
        summary.put("paymentRecords", paymentAnalyticsRepository.count());
        summary.put("reviews", reviewAnalyticsRepository.countBy());
        summary.put("topRatedMechanics", mechanicBadgeAnalyticsRepository.countByBadgesContaining("Top Rated Mechanic"));
        summary.put("reliableProfessionals", mechanicBadgeAnalyticsRepository.countByBadgesContaining("Reliable Professional"));
        summary.put("quickResponders", mechanicBadgeAnalyticsRepository.countByBadgesContaining("Quick Responder"));
        return summary;
    }

    private void recalculateBadges(UUID mechanicId, Double averageRatingOverride, Integer totalReviewsOverride) {
        if (mechanicId == null) {
            return;
        }

        MechanicBadgeAnalytics existingBadgeAnalytics = mechanicBadgeAnalyticsRepository.findByMechanicId(mechanicId)
                .orElse(null);

        double averageRating = averageRatingOverride != null
                ? averageRatingOverride
                : existingBadgeAnalytics != null && existingBadgeAnalytics.getAverageRating() != null
                ? existingBadgeAnalytics.getAverageRating()
                : 0.0;
        int totalReviews = totalReviewsOverride != null
                ? totalReviewsOverride
                : existingBadgeAnalytics != null && existingBadgeAnalytics.getTotalReviews() != null
                ? existingBadgeAnalytics.getTotalReviews()
                : 0;
        long assignedJobs = mechanicAssignmentRepository.countByMechanicId(mechanicId);
        long completedJobs = serviceCompletionRepository.countByMechanicId(mechanicId);
        double completionRate = assignedJobs == 0 ? 0.0 : (completedJobs * 100.0) / assignedJobs;
        Double averageResponseTime = mechanicAssignmentRepository.findAverageResponseTimeByMechanicId(mechanicId).stream()
                .map(MechanicAssignmentAnalyticsRepository.AverageResponseProjection::getAverage)
                .findFirst()
                .orElse(null);

        List<String> badges = new ArrayList<>();
        if (averageRating >= 4.8 && totalReviews >= 100) {
            badges.add("Top Rated Mechanic");
        }
        if (assignedJobs > 0 && completionRate >= 95.0) {
            badges.add("Reliable Professional");
        }
        if (averageResponseTime != null && averageResponseTime < 2.0) {
            badges.add("Quick Responder");
        }

        MechanicBadgeAnalytics badgeAnalytics = existingBadgeAnalytics == null
                ? new MechanicBadgeAnalytics()
                : existingBadgeAnalytics;
        badgeAnalytics.setMechanicId(mechanicId);
        badgeAnalytics.setAverageRating(roundTwoDecimals(averageRating));
        badgeAnalytics.setTotalReviews(totalReviews);
        badgeAnalytics.setCompletionRate(roundTwoDecimals(completionRate));
        badgeAnalytics.setAverageResponseTimeMins(roundNullable(averageResponseTime));
        badgeAnalytics.setBadges(badges);
        badgeAnalytics.setUpdatedAt(LocalDateTime.now());

        mechanicBadgeAnalyticsRepository.save(badgeAnalytics);
    }

    private Double roundNullable(Double value) {
        return value == null ? null : roundTwoDecimals(value);
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
