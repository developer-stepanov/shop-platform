package com.stepanov.messaging.listener;

import com.stepanov.entity.Order;
import com.stepanov.kafka.events.topics.orders.CreateOrder;
import com.stepanov.kafka.events.topics.orders.OrderAccepted;
import com.stepanov.kafka.events.topics.orders.OrderForStock;
import com.stepanov.kafka.events.topics.orders.OrderTableItemCmd;
import com.stepanov.mapper.OrderMapper;
import com.stepanov.messaging.publisher.OrderPublisher;
import com.stepanov.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.stepanov.kafka.topics.KafkaTopics.GATEWAY_COMMAND_ORDER_TOPIC;

@Service
@AllArgsConstructor
@KafkaListener(topics = GATEWAY_COMMAND_ORDER_TOPIC)
public class GatewayCmdOrderListener {

    private final OrderPublisher publisher;

    private final OrderService orderService;

    @KafkaHandler
    void on(OrderTableItemCmd evt) {
        publisher.publish(orderService.fetchOrderItems());
    }

    @KafkaHandler
    public void on(CreateOrder evt) {
        Order savedOrder = orderService.createOrder(evt);

        OrderAccepted orderAccepted = OrderMapper.toOrderAccepted(savedOrder);
        OrderForStock orderForStock = OrderMapper.toOrderForStock(savedOrder);

        publisher.publish(orderAccepted);
        publisher.publish(orderForStock);
    }

}
