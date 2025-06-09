package com.stepanov.kafka.events.topics.payments;

import com.stepanov.enums.PaymentStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentFailed(UUID orderId,
                            PaymentStatus paymentStatus) {}
