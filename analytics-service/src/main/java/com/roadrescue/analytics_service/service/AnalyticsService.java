package com.roadrescue.analytics_service.service;

import com.roadrescue.analytics_service.dto.BreakdownRequestEvent;
import com.roadrescue.analytics_service.dto.MechanicAssignmentEvent;
import com.roadrescue.analytics_service.dto.PaymentEvent;
import com.roadrescue.analytics_service.dto.ReviewEvent;
import com.roadrescue.analytics_service.dto.ServiceCompletionEvent;

import java.util.Map;

public interface AnalyticsService {
    void recordBreakdownRequest(BreakdownRequestEvent event);
    void recordMechanicAssignment(MechanicAssignmentEvent event);
    void recordServiceCompletion(ServiceCompletionEvent event);
    void recordPayment(PaymentEvent event);
    void recordReview(ReviewEvent event);
    Map<String, Object> getSummary();
}
