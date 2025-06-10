package com.stepanov.messaging;

import com.stepanov.kafka.events.topics.orders.OrderTableItemCmd;
import com.stepanov.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.stepanov.kafka.topics.KafkaTopics.GATEWAY_COMMAND_FETCH_ORDERS_TOPIC;

@Service
@AllArgsConstructor
@KafkaListener(topics = GATEWAY_COMMAND_FETCH_ORDERS_TOPIC)
public class OrderTableItemsListener {

    private final OrderPublisher orderEventsPublisher;

    private final OrderService orderService;

    @KafkaHandler
    void on(OrderTableItemCmd evt) {
        orderEventsPublisher.publish(orderService.fetchOrderItems());
    }

}
