package com.stepanov.messaging;

import com.stepanov.kafka.events.topics.stock.ItemsForSell;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import com.stepanov.kafka.events.topics.stock.OutOfStock;
import com.stepanov.kafka.events.topics.stock.StockItemUpdateQty;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.stepanov.kafka.topics.KafkaTopics.*;

@Component
@AllArgsConstructor
public class StockPublisher {

    private final KafkaTemplate<String, Object> kafka;

    public void publishStockItems(ItemsForSell evt) {
        kafka.send(ITEMS_FOR_SELL_TOPIC, evt);
    }

    public void publishReservedOrder(ConfirmationReservation evt) {
        kafka.send(ORDER_RESERVED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishOutOfStock(OutOfStock evt) {
        kafka.send(OUT_OF_STOCK_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishStockItemAvailableQtyChanged(StockItemUpdateQty evt) {
        kafka.send(STOCK_ITEM_UPDATE_TOPIC, evt.sku(), evt);
    }
}
