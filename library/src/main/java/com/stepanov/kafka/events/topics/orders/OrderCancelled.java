package com.stepanov.kafka.events.topics.orders;

import com.stepanov.enums.OrderDetails;
import com.stepanov.enums.OrderStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderCancelled(UUID orderId, OrderStatus orderStatus, OrderDetails details) {}
