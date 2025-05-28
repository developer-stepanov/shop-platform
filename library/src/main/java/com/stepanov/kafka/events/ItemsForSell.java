package com.stepanov.kafka.events;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public record ItemsForSell(List<Item> items) {
    @Builder
    public record Item(String sku,
                       BigDecimal unitPrice,
                       String name,
                       String description) {}
}
