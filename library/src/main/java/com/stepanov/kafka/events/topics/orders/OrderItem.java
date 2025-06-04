package com.stepanov.kafka.events.topics.orders;

import lombok.Builder;

@Builder
public record OrderItem(
        String sku,
        int qty
) {}
