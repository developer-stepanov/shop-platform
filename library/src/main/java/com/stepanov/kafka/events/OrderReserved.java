package com.stepanov.kafka.events;

import com.stepanov.enums.OrderStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record OrderReserved(UUID orderId, OrderStatus orderStatus, PaymentDetails paymentDetails) {
}
