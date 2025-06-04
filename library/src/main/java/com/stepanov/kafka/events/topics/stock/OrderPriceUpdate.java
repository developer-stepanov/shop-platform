package com.stepanov.kafka.events.topics.stock;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderPriceUpdate(UUID orderId, List<PriceBySku> priceBySkus) {
    @Builder
    public record PriceBySku(String sku, BigDecimal unitPrice) {}
}
