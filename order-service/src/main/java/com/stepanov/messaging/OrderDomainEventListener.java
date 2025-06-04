package com.stepanov.messaging;

import com.stepanov.enums.OrderStatus;

import com.stepanov.kafka.events.topics.orders.*;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Component;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_UPDATED_TOPIC;
import static com.stepanov.kafka.topics.KafkaTopics.PAYMENT_CREATED_TOPIC;

@Component
@RequiredArgsConstructor
public class OrderDomainEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /*
        Listens to changes in DomainEvents in OrderEntity and publish events to Kafka
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderPriceUpdate(OrderTotalAmountUpdated evt) {
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderReserved(ConfirmationReservation evt) {
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, evt.orderId().toString(), OrderReserved.builder()
                                                                            .orderId(evt.orderId())
                                                                            .orderStatus(OrderStatus.RESERVED)
                                                                            .build()); // fire -> gateway -> UI
        kafkaTemplate.send(PAYMENT_CREATED_TOPIC, evt.orderId().toString(), evt); // fire -> payment
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderCancelled(OrderCancelled evt) {
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderPaymentLinkUpdate(OrderPaymentLinkUpdate evt) {
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderPaid(OrderPaid evt) {
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }
}
