package com.stepanov.messaging.listener;

import com.stepanov.kafka.events.topics.payments.CheckoutPaymentLink;
import com.stepanov.kafka.events.topics.payments.PaymentSuccessful;

import com.stepanov.service.OrderService;
import lombok.AllArgsConstructor;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import static com.stepanov.kafka.topics.KafkaTopics.*;

@Service
@AllArgsConstructor
@KafkaListener(topics = PAYMENT_NOTIFICATION_TOPIC)
public class PaymentNotificationListener {

    private final OrderService orderService;

    @KafkaHandler
    public void on(CheckoutPaymentLink evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        orderService.paymentLink(evt);
    }

    @KafkaHandler
    public void on(PaymentSuccessful evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        orderService.orderPaymentSucceeded(evt);
    }
}
