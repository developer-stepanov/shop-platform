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
        publisher.publishOrderPriceUpdate(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ConfirmationReservation evt) {
        OrderReserved orderReserved = OrderReserved.builder()
                                            .orderId(evt.orderId())
                                            .orderStatus(OrderStatus.RESERVED)
                                            .build();
        publisher.publishOrderReserved(orderReserved);
        publisher.publishConfirmationToPayment(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderCancelled evt) {
        publisher.publishOrderCancelled(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderPaymentLinkUpdate evt) {
        publisher.publishOrderPaymentLinkUpdate(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderPaid evt) {
        publisher.publishOrderPaid(evt);
    }
}
