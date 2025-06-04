package com.stepanov.kafka.events.topics.payments;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentLink(UUID orderId, String checkoutUrl) {
}
