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

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishCheckoutLink(PaymentLink evt) {
        kafkaTemplate.send(PAYMENT_LINK_TOPIC, evt.orderId().toString(), evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishPaymentSucceeded(PaymentSuccessful evt) {
        kafkaTemplate.send(PAYMENT_SUCCESS_TOPIC, evt.orderId().toString(), evt);
    }

}
