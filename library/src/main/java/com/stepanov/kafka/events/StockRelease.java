package com.stepanov.kafka.events;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record StockRelease (
        UUID orderId,
        List<OrderItem> orderItems
) {}
