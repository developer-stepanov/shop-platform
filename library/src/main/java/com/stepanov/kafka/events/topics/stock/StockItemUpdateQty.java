package com.stepanov.kafka.events.topics.stock;

import lombok.Builder;

@Builder
public record StockItemUpdateQty(String sku, int qty) {}
