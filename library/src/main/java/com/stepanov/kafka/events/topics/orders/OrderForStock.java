package com.stepanov.kafka.events.topics.orders;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record OrderForStock(
        UUID orderId,
        List<OrderItem> orderItems
) {}
