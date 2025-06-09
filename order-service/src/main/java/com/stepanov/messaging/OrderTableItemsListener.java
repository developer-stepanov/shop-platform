package com.stepanov.messaging;

import com.stepanov.kafka.events.topics.orders.OrderTableItemCmd;
import com.stepanov.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_TABLE_ITEMS_CMD_TOPIC;

@Service
@AllArgsConstructor
@KafkaListener(topics = ORDER_TABLE_ITEMS_CMD_TOPIC)
public class OrderTableItemsListener {

    private final OrderPublisher orderEventsPublisher;

    private final OrderService orderService;

    @KafkaHandler
    void on(OrderTableItemCmd evt) {
        orderEventsPublisher.publish(orderService.fetchOrderItems());
    }

}
