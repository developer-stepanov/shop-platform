package com.stepanov.kafka.events.topics.orders;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderTotalAmountUpdated(UUID orderId, long totalAmount) {
}
