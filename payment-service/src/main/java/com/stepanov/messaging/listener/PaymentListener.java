package com.stepanov.messaging.listener;

import com.stepanov.entity.Payment;
import com.stepanov.exceptions.EmptyStripeSession;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import com.stepanov.service.PaymentService;
import com.stripe.model.checkout.Session;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_PREPARE_PAYMENT_TOPIC;

@Component
@AllArgsConstructor
@Slf4j
public class PaymentListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = ORDER_PREPARE_PAYMENT_TOPIC)
    @Transactional
    public void on(@NonNull ConfirmationReservation evt, @NonNull @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        final Payment paymentItem = paymentService.insertNewPaymentItem(evt);
        final Session stripeSession = paymentService.createCheckoutLink(paymentItem)
                                    .orElseThrow(() ->
                                            new EmptyStripeSession("Stripe session is null for OrderId: "
                                                    + evt.orderId()));

        paymentService.updatePaymentItemWithCheckoutLink(paymentItem, stripeSession);

        log.info("Prepared payment [{}] for order [{}], Stripe session [{}] created.",
                    paymentItem.getId(), evt.orderId(), stripeSession.getId());
    }
}
