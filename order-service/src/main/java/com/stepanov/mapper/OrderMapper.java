package com.stepanov.mapper;

import com.stepanov.entity.Order;
import com.stepanov.entity.OrderItem;
import com.stepanov.enums.Currency;
import com.stepanov.enums.OrderStatus;
import com.stepanov.kafka.events.topics.orders.CreateOrder;
import com.stepanov.kafka.events.topics.orders.OrderAccepted;
import com.stepanov.kafka.events.topics.orders.OrderTableItem;
import com.stepanov.kafka.events.topics.orders.ReserveStock;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

public class OrderMapper {

    public static List<OrderTableItem> fromEntity(@NonNull List<Order> orders) {
        return orders.stream()
                .map(o -> OrderTableItem.builder()
                            .orderId(o.getId())
                            .orderItems(o.getItems().stream()
                                        .map(it -> com.stepanov.kafka.events.topics.orders.OrderItem.builder()
                                                                    .sku(it.getSku())
                                                                    .qty(it.getQty())
                                                                    .build())
                                                                .toList())
                            .totalAmount(o.getTotalAmount())
                            .orderStatus(o.getStatus())
                            .details(o.getCancelReason())
                            .paymentLink(o.getStatus() != OrderStatus.PAID ? o.getPaymentLink() : null)
                            .build())
                .toList();
    }

    public static Order toEntity(@NonNull CreateOrder event) {

        final Order orderEntity = Order.builder()
                        .status(OrderStatus.CREATED)
                        .totalAmount(BigDecimal.ZERO)
                        .currency(Currency.EUR)
                        .build();

        event.orderItems().forEach(it -> {
                orderEntity.addItem(OrderItem.builder()
                                .sku(it.sku())
                                .qty(it.qty())
                                .unitPrice(BigDecimal.ZERO)
                                .build()
                );
            }
        );

        return orderEntity;
    }

    public static OrderAccepted toOrderAccepted(@NonNull Order orderEntity) {
        return OrderAccepted.builder()
                .orderId(orderEntity.getId())
                .orderItems(orderEntity.getItems().stream()
                                        .map(it -> com.stepanov.kafka.events.topics.orders.OrderItem.builder()
                                                                    .sku(it.getSku())
                                                                    .qty(it.getQty())
                                                                .build())
                                        .toList())
                .orderStatus(orderEntity.getStatus())
                .build();
    }

    public static ReserveStock toReserveStock(@NonNull Order orderEntity) {
        return ReserveStock.builder()
                .orderId(orderEntity.getId())
                .orderItems(orderEntity.getItems().stream()
                        .map(it -> com.stepanov.kafka.events.topics.orders.OrderItem.builder()
                                .sku(it.getSku())
                                .qty(it.getQty())
                                .build())
                        .toList())
                .build();
    }
}
