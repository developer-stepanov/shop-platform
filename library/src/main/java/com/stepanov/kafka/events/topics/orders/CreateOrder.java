package com.stepanov.kafka.events.topics.orders;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

// add time field to kafka events
@Builder
public record CreateOrder(
        List<OrderItem> orderItems) {}
