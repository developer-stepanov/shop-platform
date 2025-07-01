package com.stepanov.messaging.publisher;

import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import com.stepanov.kafka.events.topics.stock.ItemsForSell;
import com.stepanov.kafka.events.topics.stock.OutOfStock;
import com.stepanov.kafka.events.topics.stock.StockItemUpdateQty;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.stepanov.kafka.topics.KafkaTopics.STOCK_PRODUCT_SYNC_TOPIC;
import static com.stepanov.kafka.topics.KafkaTopics.STOCK_RESERVATION_STATUS_TOPIC;

@Component
@AllArgsConstructor
public class StockPublisher {

    private final KafkaTemplate<String, Object> kafka;

    public void publish(@NonNull ItemsForSell evt) {
        kafka.send(STOCK_PRODUCT_SYNC_TOPIC, evt);
    }

    public void publish(@NonNull StockItemUpdateQty evt) {
        kafka.send(STOCK_PRODUCT_SYNC_TOPIC, evt.sku(), evt);
    }

    public void publish(@NonNull ConfirmationReservation evt) {
        kafka.send(STOCK_RESERVATION_STATUS_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(@NonNull OutOfStock evt) {
        kafka.send(STOCK_RESERVATION_STATUS_TOPIC, evt.orderId().toString(), evt);
    }
}
