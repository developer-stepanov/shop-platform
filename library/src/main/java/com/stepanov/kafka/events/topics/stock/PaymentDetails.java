package com.stepanov.kafka.events.topics.stock;

import com.stepanov.enums.Currency;

import java.math.BigDecimal;

public record PaymentDetails(BigDecimal totalPayment, Currency currency) {}
