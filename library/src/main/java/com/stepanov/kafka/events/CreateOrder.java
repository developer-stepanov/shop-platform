package com.stepanov.kafka.events;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

// add time field to kafka events
@Builder
public record CreateOrder(
        UUID clientRequestId,
        List<OrderItem> orderItems
) {

}
