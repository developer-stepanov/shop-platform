package com.stepanov.mapper;

import com.stepanov.entity.PaymentEntity;
import com.stepanov.kafka.events.ConfirmationReservation;

public class PaymentMapper {

    public static PaymentEntity mapFrom(ConfirmationReservation evt) {
        return PaymentEntity.builder()
                .orderId(evt.orderId())
                .totalPayment(evt.paymentDetails().totalPayment())
                .currency(evt.paymentDetails().currency())
                .build();
}}
