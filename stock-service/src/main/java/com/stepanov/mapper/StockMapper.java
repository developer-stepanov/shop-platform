package com.stepanov.mapper;

import com.stepanov.entity.StockItem;
import com.stepanov.kafka.events.topics.stock.ItemsForSell;
import lombok.NonNull;

import java.util.List;

public class StockMapper {

    public static ItemsForSell fromStockItems(@NonNull List<StockItem> items) {
         return new ItemsForSell(items.stream()
                                        .map(it ->
                                            ItemsForSell.Item.builder()
                                                    .sku(it.getSku())
                                                    .name(it.getName())
                                                    .description(it.getDescription())
                                                    .unitPrice(it.getUnitPrice())
                                                    .qty(it.getAvailableQty())
                                                    .build())
                                        .toList());
    }
}
