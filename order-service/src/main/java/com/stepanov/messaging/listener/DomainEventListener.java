package com.stepanov.messaging.listener;

import com.stepanov.enums.OrderStatus;
import com.stepanov.kafka.events.topics.orders.OrderCancelled;
import com.stepanov.kafka.events.topics.orders.OrderPaid;
import com.stepanov.kafka.events.topics.orders.OrderPaymentLinkUpdate;
import com.stepanov.kafka.events.topics.orders.OrderReserved;
import com.stepanov.kafka.events.topics.orders.OrderTotalAmountUpdated;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import com.stepanov.messaging.publisher.OrderPublisher;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class DomainEventListener {

    private final OrderPublisher publisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(@NonNull OrderTotalAmountUpdated evt) {
        publisher.publish(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(@NonNull ConfirmationReservation evt) {
        final OrderReserved orderReservedEvt = OrderReserved.builder()
                                            .orderId(evt.orderId())
                                            .orderStatus(OrderStatus.RESERVED)
                                            .build();
        publisher.publish(orderReservedEvt);
        publisher.publish(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(@NonNull OrderCancelled evt) {
        publisher.publish(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(@NonNull OrderPaymentLinkUpdate evt) {
        publisher.publish(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(@NonNull OrderPaid evt) {
        publisher.publish(evt);
    }
}
