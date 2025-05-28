package com.stepanov.kafka.events;

import com.stepanov.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderAccepted(
        UUID clientRequestId,
        UUID orderId,
        List<OrderItem> orderItems,
        BigDecimal price,
        OrderStatus orderStatus
) {}
