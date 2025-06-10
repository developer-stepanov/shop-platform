package com.stepanov.kafka.events.topics.payments;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CheckoutPaymentLink(UUID orderId,
                                  String checkoutUrl) {}
