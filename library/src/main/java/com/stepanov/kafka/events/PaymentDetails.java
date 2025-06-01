package com.stepanov.kafka.events;

import com.stepanov.enums.Currency;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentDetails(BigDecimal totalPayment, Currency currency) {
}
