package com.stepanov.service;

import com.stepanov.configuration.StripeConfig;
import com.stepanov.entity.Payment;
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

    private final StripeConfig config;

    private final static String METADATA_ORDER_ID = "orderId";

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment insertNewPaymentItem(ConfirmationReservation evt) {
        Payment paymentEntity = PaymentMapper.mapFrom(evt);
        return paymentRepository.save(paymentEntity);
    }

    public Optional<Session> createCheckoutLink(Payment paymentItem) {
        Session session = null;
        try {
            session = Session.create(
                    SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(config.getPaymentConfirmationUrl())
                        .putMetadata(METADATA_ORDER_ID, paymentItem.getOrderId().toString())
                        .addLineItem(SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency(paymentItem.getCurrency().toString())
                                        .setUnitAmount(valueInCents(paymentItem.getTotalPayment().longValue()))
                                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(orderName(paymentItem.getOrderId().toString()))
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
    public void updatePaymentItemWithCheckoutLink(Payment paymentItem, Session session) {
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

    private static long valueInCents(long value) {
        return 100 * value;
    }

    private static String orderName(String name) {
        return String.format("Order: %s", name);
    }
}
