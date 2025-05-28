package com.stepanov.messaging;

import com.stepanov.kafka.events.OrderAccepted;
import com.stepanov.kafka.events.OrderForStock;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_ACCEPTED_TOPIC;
import static com.stepanov.kafka.topics.KafkaTopics.ORDER_FOR_STOCK_TOPIC;

@Component
@AllArgsConstructor
public class OrderEventsPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderAccepted(OrderAccepted orderAccepted) {
        kafkaTemplate.send(ORDER_ACCEPTED_TOPIC, orderAccepted.clientRequestId().toString(), orderAccepted);
    }

    public void publishOrderForStock(OrderForStock orderForStock) {
        kafkaTemplate.send(ORDER_FOR_STOCK_TOPIC, orderForStock.orderId().toString(), orderForStock);
    }

}
