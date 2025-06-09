package com.stepanov.mapper;

import com.stepanov.entity.Payment;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;

public class PaymentMapper {

    public static Payment mapFrom(ConfirmationReservation evt) {
        return Payment.builder()
                .orderId(evt.orderId())
                .totalPayment(evt.paymentDetails().totalPayment())
                .currency(evt.paymentDetails().currency())
                .build();
}}
