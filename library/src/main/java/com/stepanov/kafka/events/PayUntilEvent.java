package com.stepanov.kafka.events;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PayUntilEvent(UUID orderId, Instant payUntil) {
}
