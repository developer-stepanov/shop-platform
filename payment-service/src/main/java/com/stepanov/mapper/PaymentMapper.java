package com.stepanov.mapper;

import com.stepanov.entity.Payment;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import lombok.NonNull;

public class PaymentMapper {

    public static Payment mapFrom(@NonNull ConfirmationReservation evt) {
        return Payment.builder()
                .orderId(evt.orderId())
                .totalPayment(evt.paymentDetails().totalPayment())
                .currency(evt.paymentDetails().currency())
                .build();
}}
