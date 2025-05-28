package com.stepanov.kafka.events;

import lombok.Builder;

@Builder
public record OrderItem(
        String sku,
        int qty
) {}
