package com.stepanov.messaging;

import com.stepanov.enums.OrderStatus;

import com.stepanov.kafka.events.topics.orders.*;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DomainEventListener {

    private final OrderPublisher publisher;

    /*
        Listens to changes in DomainEvents in OrderEntity and publish events to Kafka
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderTotalAmountUpdated evt) {
        publisher.publish(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ConfirmationReservation evt) {
        OrderReserved orderReserved = OrderReserved.builder()
                                            .orderId(evt.orderId())
                                            .orderStatus(OrderStatus.RESERVED)
                                            .build();
        publisher.publish(orderReserved);
        publisher.publish(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderCancelled evt) {
        publisher.publish(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderPaymentLinkUpdate evt) {
        publisher.publish(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderPaid evt) {
        publisher.publish(evt);
    }
}
