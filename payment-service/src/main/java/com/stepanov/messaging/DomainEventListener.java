package com.stepanov.messaging;

import com.stepanov.kafka.events.topics.payments.PaymentLink;
import com.stepanov.kafka.events.topics.payments.PaymentSuccessful;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DomainEventListener {

    private final PaymentPublisher publisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PaymentLink evt) {
        publisher.publishCheckoutLink(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PaymentSuccessful evt) {
        publisher.publishPaymentSucceeded(evt);
    }

}
