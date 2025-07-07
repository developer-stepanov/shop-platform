package com.stepanov.messaging.listener;

import com.stepanov.entity.Order;
import com.stepanov.kafka.events.topics.orders.CreateOrder;
import com.stepanov.kafka.events.topics.orders.OrderAccepted;
import com.stepanov.kafka.events.topics.orders.OrderTableItemCmd;
import com.stepanov.kafka.events.topics.orders.ReserveStock;
import com.stepanov.mapper.OrderMapper;
import com.stepanov.messaging.publisher.OrderPublisher;
import com.stepanov.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
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
    void on(@NonNull OrderTableItemCmd evt) {
        publisher.publish(orderService.fetchOrderItems());
    }

    @KafkaHandler
    public void on(@NonNull CreateOrder evt) {
        final Order savedOrder = orderService.createOrder(evt);

        final OrderAccepted orderAccepted = OrderMapper.toOrderAccepted(savedOrder);
        final ReserveStock reserveStock = OrderMapper.toReserveStock(savedOrder);

        publisher.publish(orderAccepted);
        publisher.publish(reserveStock);
    }

}
