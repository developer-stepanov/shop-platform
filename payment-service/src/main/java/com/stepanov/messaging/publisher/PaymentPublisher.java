package com.stepanov.messaging.publisher;

import com.stepanov.kafka.events.topics.payments.CheckoutPaymentLink;
import com.stepanov.kafka.events.topics.payments.PaymentSuccessful;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.stepanov.kafka.topics.KafkaTopics.*;

@Component
@AllArgsConstructor
@Slf4j
public class PaymentPublisher {

    private final KafkaTemplate<String, Object> kafka;

    public void publish(CheckoutPaymentLink evt) {
        kafka.send(PAYMENT_NOTIFICATION_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(PaymentSuccessful evt) {
        kafka.send(PAYMENT_NOTIFICATION_TOPIC, evt.orderId().toString(), evt);
    }
}
