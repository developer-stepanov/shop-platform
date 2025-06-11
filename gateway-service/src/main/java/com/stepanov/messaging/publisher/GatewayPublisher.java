package com.stepanov.messaging.publisher;

import com.stepanov.kafka.events.topics.orders.CreateOrder;
import com.stepanov.kafka.events.topics.orders.OrderTableItemCmd;
import com.stepanov.kafka.events.topics.stock.ItemsForSellCmd;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.stepanov.kafka.topics.KafkaTopics.*;

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
