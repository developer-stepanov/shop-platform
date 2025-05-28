package com.stepanov.kafka.events;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderPriceUpdate(UUID orderId, String sku, BigDecimal unitPrice) {
}
