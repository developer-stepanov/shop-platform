package com.stepanov.controller;

import com.stepanov.kafka.events.topics.orders.CreateOrder;
import com.stepanov.kafka.events.topics.orders.OrderItem;
import com.stepanov.messaging.publisher.GatewayPublisher;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class WsController {

    private final GatewayPublisher publisher;

    @MessageMapping("/make-order")
    public void createOrder(List<OrderItem> orderItems) {
        publisher.createOrderCmd(CreateOrder.builder().orderItems(orderItems).build());
    }

    @MessageMapping("/get-product-items")
    public void fetchProducts() {
        publisher.fetchProductsCmd();
    }

    @MessageMapping("/get-order-items")
    public void fetchOrders() {
        publisher.fetchOrdersCmd();
    }

}
