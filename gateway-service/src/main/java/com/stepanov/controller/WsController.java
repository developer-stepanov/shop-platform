package com.stepanov.controller;

import com.stepanov.kafka.events.topics.orders.CreateOrder;
import com.stepanov.kafka.events.topics.orders.OrderItem;
import com.stepanov.messaging.publisher.GatewayPublisher;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * WebSocket controller that exposes a thin real-time API to the front-end page.
 *
 * <p>The controller receives STOMP messages on the websocket endpoint
 * configured in WsConfiguration.  Each handler method merely
 * delegates the work to {@link GatewayPublisher}, keeping the controller
 * free of business logic.</p>
 *
 * <h2>Message routes</h2>
 * <table border="1">
 *   <caption>Inbound message routes</caption>
 *   <tr><th>Destination</th>      <th>Action</th></tr>
 *   <tr><td><code>/make-order</code></td>      <td>Create a new order</td></tr>
 *   <tr><td><code>/get-product-items</code></td><td>Fetch the product catalogue</td></tr>
 *   <tr><td><code>/get-order-items</code></td> <td>Fetch existing orders</td></tr>
 * </table>
 *
 * @see GatewayPublisher
 *
 * @author  Maxim Stepanov
 */

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
