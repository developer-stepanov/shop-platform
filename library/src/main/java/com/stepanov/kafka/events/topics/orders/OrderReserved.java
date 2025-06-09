package com.stepanov.kafka.events.topics.orders;

import com.stepanov.enums.OrderStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderReserved(UUID orderId,
                            OrderStatus orderStatus) {}
