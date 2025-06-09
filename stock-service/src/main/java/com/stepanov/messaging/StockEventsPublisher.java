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
public class StockEventsPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishStockItems(ItemsForSell evt) {
        kafkaTemplate.send(ITEMS_FOR_SELL_TOPIC, evt);
    }

    public void publishReservedOrder(ConfirmationReservation evt) {
        kafkaTemplate.send(ORDER_RESERVED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishOutOfStock(OutOfStock evt) {
        kafkaTemplate.send(OUT_OF_STOCK_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishStockItemAvailableQtyChanged(StockItemUpdateQty evt) {
        kafkaTemplate.send(STOCK_ITEM_UPDATE_TOPIC, evt.sku(), evt);
    }
}
