package com.stepanov.messaging.publisher;

import com.stepanov.kafka.events.topics.orders.CreateOrder;
import com.stepanov.kafka.events.topics.orders.OrderTableItemCmd;
import com.stepanov.kafka.events.topics.stock.ItemsForSellCmd;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.stepanov.kafka.topics.KafkaTopics.GATEWAY_COMMAND_ORDER_TOPIC;
import static com.stepanov.kafka.topics.KafkaTopics.GATEWAY_COMMAND_STOCK_TOPIC;

/**
 * Publishes gateway-level commands to Kafka so that downstream micro-services
 * can react asynchronously.
 *
 * <p>The service is a thin wrapper around {@link KafkaTemplate}; it adds no
 * business logic and exists mainly to keep messaging concerns out of
 * WebSocket controllers.</p>
 *
 * <h2>Command topics</h2>
 * <ul>
 *   <li><strong>{@value com.stepanov.kafka.topics.KafkaTopics#GATEWAY_COMMAND_ORDER_TOPIC}</strong> – commands related to orders</li>
 *   <li><strong>{@value com.stepanov.kafka.topics.KafkaTopics#GATEWAY_COMMAND_STOCK_TOPIC}</strong> – commands related to stock / products</li>
 * </ul>
 *
 * @author Maxim Stepanov
 *
 */

@Service
@AllArgsConstructor
public class GatewayPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void createOrderCmd(CreateOrder evt) {
        kafkaTemplate.send(GATEWAY_COMMAND_ORDER_TOPIC, evt);
    }

    public void fetchOrdersCmd() {
        kafkaTemplate.send(GATEWAY_COMMAND_ORDER_TOPIC, new OrderTableItemCmd());
    }

    public void fetchProductsCmd() {
        kafkaTemplate.send(GATEWAY_COMMAND_STOCK_TOPIC, new ItemsForSellCmd());
    }

}
