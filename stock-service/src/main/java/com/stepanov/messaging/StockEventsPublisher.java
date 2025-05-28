package com.stepanov.messaging;

import com.stepanov.kafka.events.ItemsForSell;
import com.stepanov.kafka.events.OrderPriceUpdate;
import com.stepanov.kafka.events.OrderReserved;
import com.stepanov.kafka.events.OutOfStock;
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

    public void publishUpdatedPrice(OrderPriceUpdate evt) {
        kafkaTemplate.send(ORDER_PRICE_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishReservedOrder(OrderReserved evt) {
        kafkaTemplate.send(ORDER_RESERVED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishOutOfStock(OutOfStock evt) {
        kafkaTemplate.send(OUT_OF_STOCK_TOPIC, evt.orderId().toString(), evt);
    }
}
