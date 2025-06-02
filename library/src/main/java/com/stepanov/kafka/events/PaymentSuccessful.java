package com.stepanov.kafka.events;

import com.stepanov.enums.PaymentStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentSuccessful(UUID orderId, PaymentStatus paymentStatus) {
}
