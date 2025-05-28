package com.stepanov.kafka.events;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record OrderForStock(
        UUID orderId,
        List<OrderItem> orderItems
) {}
