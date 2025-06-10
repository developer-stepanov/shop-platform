package com.stepanov.messaging;

import com.stepanov.kafka.events.topics.orders.*;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.stepanov.kafka.topics.KafkaTopics.*;

@Component
@AllArgsConstructor
public class OrderPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(List<OrderTableItem> evt) {
        kafkaTemplate.send(ORDER_FETCHED_ORDERS_TOPIC, evt);
    }

    public void publish(OrderAccepted evt) {
        kafkaTemplate.send(ORDER_ORDER_ACCEPTED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(OrderForStock evt) {
        kafkaTemplate.send(ORDER_RESERVE_ORDER_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(StockRelease evt) {
        kafkaTemplate.send(ORDER_RELEASE_STOCK_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(ConfirmationReservation evt) {
        kafkaTemplate.send(ORDER_NOTIFY_PAYMENT_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(OrderTotalAmountUpdated evt) {
        kafkaTemplate.send(ORDER_ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(OrderReserved evt) {
        kafkaTemplate.send(ORDER_ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(OrderCancelled evt) {
        kafkaTemplate.send(ORDER_ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(OrderPaymentLinkUpdate evt) {
        kafkaTemplate.send(ORDER_ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(OrderPaid evt) {
        kafkaTemplate.send(ORDER_ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

}
