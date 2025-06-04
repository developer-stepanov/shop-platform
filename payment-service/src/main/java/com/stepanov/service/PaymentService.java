package com.stepanov.service;

import com.stepanov.entity.PaymentEntity;
import com.stepanov.enums.PaymentStatus;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import com.stepanov.mapper.PaymentMapper;
import com.stepanov.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentService {

    private final static String METADATA_ORDER_ID = "orderId";

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentEntity insertNewPaymentItem(ConfirmationReservation evt) {
        PaymentEntity paymentEntity = PaymentMapper.mapFrom(evt);
        return paymentRepository.save(paymentEntity);
    }

    public Optional<Session> createCheckoutLink(PaymentEntity paymentItem) {
        Session session = null;
        try {
            session = Session.create(
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl("http://localhost:8080/payment/confirmation") // change to gateway controller to show that
                                                                    // payment is successful
                            .putMetadata(METADATA_ORDER_ID, paymentItem.getOrderId().toString())
                            .addLineItem(SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency(paymentItem.getCurrency().toString())
                                            // value in cents
                                            .setUnitAmount(100 * paymentItem.getTotalPayment().longValue())
                                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName("Order: " + paymentItem.getOrderId().toString())
                                                    .build())
                                            .build())
                                    .build())
                            .build()
            );
        } catch (StripeException e) {
            log.error("Stripe payment session can not be created for orderId: {}", paymentItem.getOrderId());
        }

        return Optional.ofNullable(session);
    }

    @Transactional
    public void updatePaymentItemWithCheckoutLink(PaymentEntity paymentItem, Session session) {
        paymentItem.updateWithCheckoutLink(session);
        paymentRepository.save(paymentItem);
    }

    @Transactional
    public void markPaymentSucceeded(Session session) {
        paymentRepository.findByStripeSessionId(session.getId()).ifPresent(it -> {
            if (it.getPaymentStatus() == PaymentStatus.SUCCEEDED) {
                // log there that webhook calls once again for the same orderId
                return; // -> idempotent
            }

            it.markPaymentAsSucceeded(session.getPaymentIntent());
            paymentRepository.save(it);
        });
    }
}
