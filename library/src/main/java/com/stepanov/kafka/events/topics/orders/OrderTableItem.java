package com.stepanov.kafka.events.topics.orders;

import com.stepanov.enums.OrderDetails;
import com.stepanov.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderTableItem(UUID orderId,
                             List<OrderItem> orderItems,
                             BigDecimal totalAmount,
                             OrderStatus orderStatus,
                             OrderDetails details,
                             String paymentLink) {}
