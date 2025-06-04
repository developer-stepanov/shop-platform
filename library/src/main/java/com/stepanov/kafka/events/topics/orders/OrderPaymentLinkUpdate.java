package com.stepanov.kafka.events.topics.orders;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderPaymentLinkUpdate(UUID orderId, String paymentLink) {
}
