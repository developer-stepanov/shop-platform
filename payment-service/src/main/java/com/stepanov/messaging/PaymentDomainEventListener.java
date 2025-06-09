package com.stepanov.messaging;

import com.stepanov.kafka.events.topics.payments.PaymentLink;
import com.stepanov.kafka.events.topics.payments.PaymentSuccessful;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.stepanov.kafka.topics.KafkaTopics.PAYMENT_LINK_TOPIC;
import static com.stepanov.kafka.topics.KafkaTopics.PAYMENT_SUCCESS_TOPIC;

@Component
@RequiredArgsConstructor
public class PaymentDomainEventListener {

    private final PaymentsPublisher publisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PaymentLink evt) {
        publisher.publishCheckoutLink(evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PaymentSuccessful evt) {
        publisher.publishPaymentSucceeded(evt);
    }

}
