package com.stepanov.messaging.listener;

import com.stepanov.enums.OrderStatus;
import com.stepanov.kafka.events.topics.orders.OrderCancelled;
import com.stepanov.kafka.events.topics.orders.OrderPaid;
import com.stepanov.kafka.events.topics.orders.OrderPaymentLinkUpdate;
import com.stepanov.kafka.events.topics.orders.OrderReserved;
import com.stepanov.kafka.events.topics.orders.OrderTotalAmountUpdated;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import com.stepanov.messaging.publisher.OrderPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
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
