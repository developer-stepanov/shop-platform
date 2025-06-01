package com.stepanov.service;

import com.stepanov.entity.PaymentEntity;
import com.stepanov.enums.PaymentStatus;
import com.stepanov.kafka.events.OrderReserved;
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
    public PaymentEntity insertNewPaymentItem(OrderReserved evt) {
        PaymentEntity paymentEntity = PaymentMapper.mapFrom(evt);
        return paymentRepository.save(paymentEntity);
    }

    public Optional<Session> createCheckoutLink(PaymentEntity paymentItem) {
        Session session = null;
        try {
            session = Session.create(
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl("http://localhost:9999")
                            .putMetadata(METADATA_ORDER_ID, paymentItem.getOrderId().toString())
                            .addLineItem(SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency(paymentItem.getCurrency().toString())
                                            .setUnitAmount(paymentItem.getTotalPayment().longValue())
                                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName("Order: " + paymentItem.getOrderId().toString())
                                                    .build())
                                            .build())
                                    .build())
                            .build()
            );
        } catch (StripeException e) {
            log.error("Payment session can not created for orderId: {}", paymentItem.getOrderId());
        }

        return Optional.ofNullable(session);
    }

    @Transactional
    public void updatePaymentItem(PaymentEntity paymentItem, Session session) {
        paymentItem.setStripeSessionId(session.getId());
        paymentItem.setStripeCheckoutUrl(session.getUrl());
        paymentItem.setPaymentStatus(PaymentStatus.LINK_SENT);

        paymentRepository.save(paymentItem);
    }
}
