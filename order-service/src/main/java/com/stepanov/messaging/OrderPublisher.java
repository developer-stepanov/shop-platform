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

    public void publishOrderTableItems(List<OrderTableItem> evt) {
        kafkaTemplate.send(ORDER_TABLE_ITEMS_TOPIC, evt);
    }

    public void publishOrderAccepted(OrderAccepted evt) {
        kafkaTemplate.send(ORDER_ACCEPTED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishOrderForStock(OrderForStock evt) {
        kafkaTemplate.send(ORDER_FOR_STOCK_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishStockRelease(StockRelease evt) {
        kafkaTemplate.send(STOCK_RELEASE_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishOrderPriceUpdate(OrderTotalAmountUpdated evt) {
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishOrderReserved(OrderReserved evt) {
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishOrderCancelled(OrderCancelled evt) {
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishOrderPaymentLinkUpdate(OrderPaymentLinkUpdate evt) {
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishOrderPaid(OrderPaid evt) {
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    public void publishConfirmationToPayment(ConfirmationReservation evt) {
        kafkaTemplate.send(PAYMENT_CREATED_TOPIC, evt.orderId().toString(), evt);
    }

}
