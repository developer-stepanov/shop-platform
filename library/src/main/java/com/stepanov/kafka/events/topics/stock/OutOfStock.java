package com.stepanov.kafka.events.topics.stock;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OutOfStock(UUID orderId) {}
