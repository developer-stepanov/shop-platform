package com.stepanov.controller;

import com.stepanov.kafka.events.CreateOrder;
import com.stepanov.kafka.events.OrderItem;
import com.stepanov.messaging.publisher.GatewayEventsPublisher;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class WsController {

    private final GatewayEventsPublisher publisher;

    @MessageMapping("/make-order")
    public void handleCreateOrderEvent(List<OrderItem> orderItems) {
        publisher.publishCreateOrder(CreateOrder.builder()
                                        .clientRequestId(UUID.randomUUID())
                                        .orderItems(orderItems)
                                     .build());
    }

    @MessageMapping("/get-product-items")
    public void handleItemsForSellEvent() {
        publisher.publishGiveItemsForSell();
    }

    @MessageMapping("/get-order-items")
    public void handleOrderItemsEvent() {
        publisher.publishOrderTableItems();
    }

}
