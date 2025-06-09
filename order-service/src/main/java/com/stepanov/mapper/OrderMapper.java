package com.stepanov.mapper;

import com.stepanov.entity.Order;

import com.stepanov.entity.OrderItem;
import com.stepanov.enums.Currency;
import com.stepanov.enums.OrderStatus;
import com.stepanov.kafka.events.topics.orders.*;

import java.math.BigDecimal;
import java.util.List;

public class OrderMapper {

    public static List<OrderTableItem> fromEntity(List<Order> orders) {
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

    public static Order toEntity(CreateOrder event) {

        Order orderEntity = Order.builder()
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

    public static OrderAccepted toOrderAccepted(Order orderEntity) {
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

    public static OrderForStock toOrderForStock(Order orderEntity) {
        return OrderForStock.builder()
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
