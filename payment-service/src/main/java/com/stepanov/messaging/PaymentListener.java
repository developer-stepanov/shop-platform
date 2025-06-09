package com.stepanov.messaging;

import com.stepanov.entity.Payment;
import com.stepanov.exceptions.EmptyStripeSession;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import com.stepanov.service.PaymentService;
import com.stripe.model.checkout.Session;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.stepanov.kafka.topics.KafkaTopics.PAYMENT_CREATED_TOPIC;

@Component
@AllArgsConstructor
@Slf4j
public class PaymentListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = PAYMENT_CREATED_TOPIC)
    @Transactional // make atomic with Kafka changes
    public void on(ConfirmationReservation evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        Payment paymentItem = paymentService.insertNewPaymentItem(evt);
        Session stripeSession = paymentService.createCheckoutLink(paymentItem)
                                    .orElseThrow(() -> new EmptyStripeSession("OrderId: " + evt.orderId()));

        paymentService.updatePaymentItemWithCheckoutLink(paymentItem, stripeSession);
    }
}
