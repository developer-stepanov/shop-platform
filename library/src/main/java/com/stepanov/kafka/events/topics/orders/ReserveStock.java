package com.stepanov.kafka.events.topics.orders;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ReserveStock(
        UUID orderId,
        List<OrderItem> orderItems) {}
