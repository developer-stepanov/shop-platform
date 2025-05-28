package com.stepanov.mapper;

import com.stepanov.entity.StockItemEntity;
import com.stepanov.kafka.events.ItemsForSell;

import java.util.List;

public class StockMapper {

    public static ItemsForSell fromStockItems(List<StockItemEntity> items) {
         return new ItemsForSell(items.stream()
                                        .map(it ->
                                            ItemsForSell.Item.builder()
                                                    .sku(it.getSku())
                                                    .name(it.getName())
                                                    .description(it.getDescription())
                                                    .unitPrice(it.getUnitPrice())
                                                    .build())
                                        .toList());
    }
}
