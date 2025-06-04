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
public class GatewayEventsPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCreateOrder(CreateOrder evt) {
        kafkaTemplate.send(ORDER_CREATED_TOPIC, evt.clientRequestId().toString(), evt);
    }

    public void publishGiveItemsForSell() {
        kafkaTemplate.send(GIVE_ITEMS_FOR_SELL_TOPIC, new ItemsForSellCmd());
    }

    public void publishOrderTableItems() {
        kafkaTemplate.send(ORDER_TABLE_ITEMS_CMD_TOPIC, new OrderTableItemCmd());
    }

}
