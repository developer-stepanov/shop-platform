package com.stepanov.messaging;

import com.stepanov.entity.Order;
import com.stepanov.kafka.events.topics.orders.CreateOrder;
import com.stepanov.kafka.events.topics.orders.OrderAccepted;
import com.stepanov.kafka.events.topics.orders.OrderForStock;
import com.stepanov.kafka.events.topics.payments.PaymentLink;
import com.stepanov.kafka.events.topics.payments.PaymentSuccessful;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import com.stepanov.kafka.events.topics.stock.OutOfStock;
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

    @KafkaListener(topics = ORDER_CREATED_TOPIC)
    @Transactional // make atomic with Kafka changes
    public void on(CreateOrder evt) {

        Order savedOrder = orderService.createOrder(evt);

        OrderAccepted orderAccepted = OrderMapper.toOrderAccepted(savedOrder);
        OrderForStock orderForStock = OrderMapper.toOrderForStock(savedOrder);

        publisher.publishOrderAccepted(orderAccepted);
        publisher.publishOrderForStock(orderForStock);
    }

    @KafkaListener(topics = ORDER_RESERVED_TOPIC)
    @Transactional
    public void on(ConfirmationReservation evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        orderService.orderReserved(evt);
    }

    @KafkaListener(topics = OUT_OF_STOCK_TOPIC)
    @Transactional
    public void on(OutOfStock evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
       orderService.orderOutOfStock(evt);
    }

    @KafkaListener(topics = PAYMENT_LINK_TOPIC)
    @Transactional
    public void on(PaymentLink evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        orderService.paymentLink(evt);
    }

    @KafkaListener(topics = PAYMENT_SUCCESS_TOPIC)
    @Transactional
    public void on(PaymentSuccessful evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        orderService.orderPaymentSucceeded(evt);
    }
}
