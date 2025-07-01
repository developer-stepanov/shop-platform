package com.stepanov.service;

import com.stepanov.configuration.StripeConfig;
import com.stepanov.entity.Payment;
import com.stepanov.enums.PaymentStatus;
import com.stepanov.exceptions.PaymentStripeException;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import com.stepanov.mapper.PaymentMapper;
import com.stepanov.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AllArgsConstructor;
import lombok.NonNull;
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
    public Payment insertNewPaymentItem(@NonNull ConfirmationReservation evt) {
        final Payment paymentEntity = PaymentMapper.mapFrom(evt);
        return paymentRepository.save(paymentEntity);
    }

    public Optional<Session> createCheckoutLink(@NonNull Payment paymentItem) {
        try {
            final Session session = buildStripeSession(config, paymentItem);
            return Optional.ofNullable(session);
        } catch (StripeException e) {
            log.error("Stripe payment session cannot be created for orderId: {}", paymentItem.getOrderId(), e);
            throw new PaymentStripeException(
                    String.format("Error during creating Stripe payment session!\n" +
                                  "error message: [%s]\nfor order: [%s]", e.getMessage(), paymentItem.getOrderId())
            );
        }
    }

    @Transactional
    public void updatePaymentItemWithCheckoutLink(@NonNull Payment paymentItem, @NonNull Session session) {
        paymentItem.updateWithCheckoutLink(session);
        paymentRepository.save(paymentItem);
    }

    @Transactional
    public void markPaymentSucceeded(@NonNull Session session) {
        paymentRepository.findByStripeSessionId(session.getId())
            .ifPresentOrElse(it -> {
                if (it.getPaymentStatus() == PaymentStatus.SUCCEEDED) {
                    log.warn("Webhook calls one more time to mark payment as SUCCEEDED for orderId: {}",
                            it.getOrderId());
                    return; // -> idempotent
                }

                it.markPaymentAsSucceeded(session.getPaymentIntent());
                paymentRepository.save(it);
            }, () -> log.error("Can't find Payment by stripe session id {}", session.getId()));
    }

    private static long valueInCents(long value) {
        return 100 * value;
    }

    private static String orderName(@NonNull String name) {
        return String.format("Order: %s", name);
    }

    private static Session buildStripeSession(@NonNull StripeConfig config, @NonNull Payment paymentItem) throws StripeException {
        return Session.create(
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
                        .build());
    }
}
