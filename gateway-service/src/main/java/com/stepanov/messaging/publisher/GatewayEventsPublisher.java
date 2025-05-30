package com.stepanov.messaging.publisher;

import com.stepanov.kafka.events.CreateOrder;
import com.stepanov.kafka.events.GiveItemsForSell;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_CREATED_TOPIC;
import static com.stepanov.kafka.topics.KafkaTopics.GIVE_ITEMS_FOR_SELL_TOPIC;

@Service
@AllArgsConstructor
public class GatewayEventsPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCreateOrder(CreateOrder evt) {
        kafkaTemplate.send(ORDER_CREATED_TOPIC, evt.clientRequestId().toString(), evt);
    }

    public void publishGiveItemsForSell() {
        kafkaTemplate.send(GIVE_ITEMS_FOR_SELL_TOPIC, new GiveItemsForSell());
    }

}
