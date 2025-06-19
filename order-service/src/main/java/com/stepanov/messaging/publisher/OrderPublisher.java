package com.stepanov.messaging.publisher;

import com.stepanov.kafka.events.topics.orders.OrderAccepted;
import com.stepanov.kafka.events.topics.orders.OrderCancelled;
import com.stepanov.kafka.events.topics.orders.OrderForStock;
import com.stepanov.kafka.events.topics.orders.OrderPaid;
import com.stepanov.kafka.events.topics.orders.OrderPaymentLinkUpdate;
import com.stepanov.kafka.events.topics.orders.OrderReserved;
import com.stepanov.kafka.events.topics.orders.OrderTableItem;
import com.stepanov.kafka.events.topics.orders.OrderTotalAmountUpdated;
import com.stepanov.kafka.events.topics.orders.StockRelease;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_COMMAND_STOCK_TOPIC;
import static com.stepanov.kafka.topics.KafkaTopics.ORDER_ORDER_SYNC_TOPIC;
import static com.stepanov.kafka.topics.KafkaTopics.ORDER_PREPARE_PAYMENT_TOPIC;

@Service
@AllArgsConstructor
public class OrderPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(OrderForStock evt) {
        kafkaTemplate.send(ORDER_COMMAND_STOCK_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(StockRelease evt) {
        kafkaTemplate.send(ORDER_COMMAND_STOCK_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(ConfirmationReservation evt) {
        kafkaTemplate.send(ORDER_PREPARE_PAYMENT_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(List<OrderTableItem> evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt);
    }

    public void publish(OrderAccepted evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(OrderTotalAmountUpdated evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(OrderReserved evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(OrderCancelled evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(OrderPaymentLinkUpdate evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(OrderPaid evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt.orderId().toString(), evt);
    }

}
