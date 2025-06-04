package com.stepanov.messaging.listener;

import com.stepanov.kafka.events.topics.orders.OrderTableItem;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_TABLE_ITEMS_TOPIC;

@Service
@AllArgsConstructor
@KafkaListener(topics = ORDER_TABLE_ITEMS_TOPIC)
public class OrderTableItemsTopicListener {

    private final SimpMessagingTemplate broker;

    @KafkaHandler
    void on(List<OrderTableItem> evt) {

        broker.convertAndSend("/topic/events",
                evt,
                Map.of("event-type", evt.getClass().getSimpleName()));
    }
}
