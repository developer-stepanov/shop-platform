package com.stepanov.kafka.events.topics.orders;

import com.stepanov.enums.OrderStatus;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record OrderAccepted(
        UUID clientRequestId,
        UUID orderId,
        List<OrderItem> orderItems,
        OrderStatus orderStatus
) {}
