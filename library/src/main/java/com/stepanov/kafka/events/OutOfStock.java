package com.stepanov.kafka.events;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OutOfStock(UUID orderId) {
}
