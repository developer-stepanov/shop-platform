package com.stepanov.messaging;

import com.stepanov.kafka.events.topics.orders.OrderAccepted;
import com.stepanov.kafka.events.topics.orders.OrderForStock;
import com.stepanov.kafka.events.topics.orders.OrderTableItem;
import com.stepanov.kafka.events.topics.orders.StockRelease;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.stepanov.kafka.topics.KafkaTopics.*;

@Component
@AllArgsConstructor
public class OrderEventsPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderTableItems(List<OrderTableItem> evt) {
        kafkaTemplate.send(ORDER_TABLE_ITEMS_TOPIC, evt);
    }

    public void publishOrderAccepted(OrderAccepted evt) {
        kafkaTemplate.send(ORDER_ACCEPTED_TOPIC, evt.clientRequestId().toString(), evt);
    }

    public void publishOrderForStock(OrderForStock evt) {
        kafkaTemplate.send(ORDER_FOR_STOCK_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishStockRelease(StockRelease evt) {
        kafkaTemplate.send(STOCK_RELEASE_TOPIC, evt.orderId().toString(), evt);
    }

}
