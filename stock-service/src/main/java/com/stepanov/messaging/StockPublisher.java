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

    public void publish(ItemsForSell evt) {
        kafka.send(STOCK_PRODUCT_SYNC_TOPIC, evt);
    }

    public void publish(StockItemUpdateQty evt) {
        kafka.send(STOCK_PRODUCT_SYNC_TOPIC, evt.sku(), evt);
    }

    public void publish(ConfirmationReservation evt) {
        kafka.send(STOCK_RESERVATION_STATUS_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(OutOfStock evt) {
        kafka.send(STOCK_RESERVATION_STATUS_TOPIC, evt.orderId().toString(), evt);
    }
}
