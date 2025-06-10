package com.stepanov.messaging;

import com.stepanov.entity.Order;
import com.stepanov.kafka.events.topics.orders.CreateOrder;
import com.stepanov.kafka.events.topics.orders.OrderAccepted;
import com.stepanov.kafka.events.topics.orders.OrderForStock;
import com.stepanov.kafka.events.topics.payments.CheckoutPaymentLink;
import com.stepanov.kafka.events.topics.payments.PaymentSuccessful;
import com.stepanov.mapper.OrderMapper;

import com.stepanov.service.OrderService;
import lombok.AllArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.stepanov.kafka.topics.KafkaTopics.*;

@Component
@AllArgsConstructor
public class OrderListener {

    private final OrderPublisher publisher;

    private final OrderService orderService;

    @KafkaListener(topics = GATEWAY_COMMAND_CREATE_ORDER_TOPIC)
    @Transactional // make atomic with Kafka changes
    public void on(CreateOrder evt) {

        Order savedOrder = orderService.createOrder(evt);

        OrderAccepted orderAccepted = OrderMapper.toOrderAccepted(savedOrder);
        OrderForStock orderForStock = OrderMapper.toOrderForStock(savedOrder);

        publisher.publish(orderAccepted);
        publisher.publish(orderForStock);
    }

    @KafkaListener(topics = PAYMENT_CHECKOUT_PAYMENT_LINK_TOPIC)
    @Transactional
    public void on(CheckoutPaymentLink evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        orderService.paymentLink(evt);
    }

    @KafkaListener(topics = PAYMENT_PAYMENT_STATUS_TOPIC)
    @Transactional
    public void on(PaymentSuccessful evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        orderService.orderPaymentSucceeded(evt);
    }
}
