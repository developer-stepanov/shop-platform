package com.stepanov.kafka.events.topics.stock;

import com.stepanov.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record ConfirmationReservation(UUID orderId,
                                      OrderStatus orderStatus,
                                      PaymentDetails paymentDetails,
                                      List<UnitPrice> unitPrices) {

    public record UnitPrice(String sku, BigDecimal unitPrice) {}
}
