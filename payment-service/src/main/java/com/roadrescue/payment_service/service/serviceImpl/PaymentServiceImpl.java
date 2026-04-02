package com.roadrescue.payment_service.service.serviceImpl;

import com.roadrescue.payment_service.dto.MechanicAssignmentEvent;
import com.roadrescue.payment_service.dto.PaymentEvent;
import com.roadrescue.payment_service.dto.PaymentSummaryDTO;
import com.roadrescue.payment_service.dto.ProcessPaymentRequest;
import com.roadrescue.payment_service.dto.ServiceCompletionEvent;
import com.roadrescue.payment_service.model.PaymentRecord;
import com.roadrescue.payment_service.model.PaymentStatus;
import com.roadrescue.payment_service.repository.PaymentRecordRepository;
import com.roadrescue.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRecordRepository paymentRecordRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${payments.platform-fee-percent:10}")
    private Double platformFeePercent;

    @Value("${payments.default-deposit-hold:200}")
    private Double defaultDepositHold;

    @Value("${spring.kafka.topic.payments-topic}")
    private String paymentsTopic;

    @Override
    @Transactional
    public void createEstimatedPaymentHold(MechanicAssignmentEvent event) {
        PaymentRecord record = paymentRecordRepository.findByRequestId(event.getRequestId())
                .orElseGet(() -> PaymentRecord.builder()
                        .requestId(event.getRequestId())
                        .customerId(event.getCustomerId())
                        .mechanicId(event.getMechanicId())
                        .build());

        record.setMechanicId(event.getMechanicId());
        record.setCustomerId(event.getCustomerId());
        record.setEstimatedAmount(roundCurrency(event.getEstimatedAmount() == null ? 0.0 : event.getEstimatedAmount()));
        record.setDepositHoldAmount(roundCurrency(
                event.getDepositHoldAmount() == null ? defaultDepositHold : event.getDepositHoldAmount()
        ));
        record.setDepositHeld(Boolean.TRUE);
        if (record.getDepositHeldAt() == null) {
            record.setDepositHeldAt(LocalDateTime.now());
        }
        record.setDepositReleasedAt(null);
        if (record.getStatus() == null) {
            record.setStatus(PaymentStatus.PENDING);
        }

        paymentRecordRepository.save(record);
        log.info("Created estimated payment hold for request {} with deposit Rs {}",
                event.getRequestId(), record.getDepositHoldAmount());
    }

    @Override
    @Transactional
    public void createPendingPayment(ServiceCompletionEvent event) {
        PaymentRecord record = paymentRecordRepository.findByRequestId(event.getRequestId())
                .orElseGet(() -> PaymentRecord.builder()
                        .requestId(event.getRequestId())
                        .customerId(event.getCustomerId())
                        .mechanicId(event.getMechanicId())
                        .build());

        double totalAmount = event.getTotalAmount() == null ? 0.0 : event.getTotalAmount();
        double platformFee = roundCurrency(totalAmount * (platformFeePercent / 100.0));
        double mechanicEarning = roundCurrency(totalAmount - platformFee);

        record.setServiceDurationMins(event.getServiceDurationMins());
        record.setLaborCharge(event.getLaborCharge());
        record.setPartsCharge(event.getPartsCharge());
        record.setTotalAmount(totalAmount);
        record.setPlatformFee(platformFee);
        record.setMechanicEarning(mechanicEarning);
        record.setStatus(PaymentStatus.PENDING);
        record.setPaidAt(null);
        record.setPaymentGateway(null);
        record.setGatewayReference(null);
        record.setGatewayOrderId(null);
        record.setGatewayPaymentId(null);
        record.setGatewaySignature(null);
        record.setCurrency(null);

        paymentRecordRepository.save(record);
        log.info("Created pending payment record for request {}", event.getRequestId());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentSummaryDTO getPaymentByRequestId(UUID requestId) {
        PaymentRecord record = paymentRecordRepository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found"));
        return toSummary(record);
    }

    @Override
    @Transactional
    public PaymentSummaryDTO processPayment(UUID requestId, ProcessPaymentRequest request) {
        PaymentRecord record = paymentRecordRepository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found"));

        record.setStatus(PaymentStatus.SUCCESS);
        record.setPaymentGateway(request.getPaymentGateway().trim());
        record.setGatewayReference("SIM-" + UUID.randomUUID());
        record.setDepositHeld(Boolean.FALSE);
        record.setDepositReleasedAt(LocalDateTime.now());
        record.setPaidAt(LocalDateTime.now());
        paymentRecordRepository.save(record);

        PaymentEvent event = PaymentEvent.builder()
                .requestId(record.getRequestId())
                .paymentId(record.getId())
                .customerId(record.getCustomerId())
                .mechanicId(record.getMechanicId())
                .amount(record.getTotalAmount())
                .mechanicEarning(record.getMechanicEarning())
                .platformFee(record.getPlatformFee())
                .status(record.getStatus().name())
                .paidAt(record.getPaidAt())
                .build();

        kafkaTemplate.send(paymentsTopic, requestId.toString(), event);
        log.info("Processed payment for request {} via {}", requestId, record.getPaymentGateway());

        return toSummary(record);
    }

    private PaymentSummaryDTO toSummary(PaymentRecord record) {
        return PaymentSummaryDTO.builder()
                .paymentId(record.getId())
                .requestId(record.getRequestId())
                .estimatedAmount(record.getEstimatedAmount())
                .depositHoldAmount(record.getDepositHoldAmount())
                .depositHeld(record.getDepositHeld())
                .depositHeldAt(record.getDepositHeldAt())
                .depositReleasedAt(record.getDepositReleasedAt())
                .laborCharge(record.getLaborCharge())
                .partsCharge(record.getPartsCharge())
                .totalAmount(record.getTotalAmount())
                .platformFee(record.getPlatformFee())
                .mechanicEarning(record.getMechanicEarning())
                .paymentGateway(record.getPaymentGateway())
                .gatewayOrderId(record.getGatewayOrderId())
                .gatewayPaymentId(record.getGatewayPaymentId())
                .currency(record.getCurrency())
                .status(record.getStatus().name())
                .paidAt(record.getPaidAt())
                .build();
    }

    private double roundCurrency(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
}
