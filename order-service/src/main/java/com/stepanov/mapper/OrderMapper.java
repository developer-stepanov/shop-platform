package com.stepanov.mapper;

import com.stepanov.entity.OrderEntity;

import com.stepanov.entity.OrderItemEntity;
import com.stepanov.enums.Currency;
import com.stepanov.enums.OrderStatus;
import com.stepanov.kafka.events.CreateOrder;
import com.stepanov.kafka.events.OrderAccepted;
import com.stepanov.kafka.events.OrderForStock;
import com.stepanov.kafka.events.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderMapper {

    public static OrderEntity toEntity(CreateOrder event) {

        OrderEntity orderEntity = OrderEntity.builder()
                        .status(OrderStatus.CREATED)
                        .totalAmount(BigDecimal.ZERO)
                        .currency(Currency.EUR)
                        .build();

        event.orderItems().forEach(it -> {
                orderEntity.addItem(OrderItemEntity.builder()
                                .sku(it.sku())
                                .qty(it.qty())
                                .unitPrice(BigDecimal.ZERO)
                                .build()
                );
            }
        );

        return orderEntity;
    }

    public static OrderAccepted toOrderAccepted(UUID clientRequestId, OrderEntity orderEntity) {
        return OrderAccepted.builder()
                .clientRequestId(clientRequestId)
                .orderId(orderEntity.getId())
                .orderItems(orderEntity.getItems().stream()
                                        .map(it -> OrderItem.builder()
                                                                    .sku(it.getSku())
                                                                    .qty(it.getQty())
                                                                .build())
                                        .toList())
                .orderStatus(orderEntity.getStatus())
                .build();
    }

    public static OrderForStock toOrderForStock(OrderEntity orderEntity) {
        return OrderForStock.builder()
                .orderId(orderEntity.getId())
                .orderItems(orderEntity.getItems().stream()
                        .map(it -> OrderItem.builder()
                                .sku(it.getSku())
                                .qty(it.getQty())
                                .build())
                        .toList())
                .build();
    }
}
