package com.stepanov.kafka.events.topics.orders;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PayUntil(UUID orderId, Instant payUntil) {
}
