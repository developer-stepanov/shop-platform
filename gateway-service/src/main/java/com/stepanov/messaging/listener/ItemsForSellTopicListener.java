package com.stepanov.messaging.listener;

import com.stepanov.kafka.events.ItemsForSell;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.stepanov.kafka.topics.KafkaTopics.ITEMS_FOR_SELL_TOPIC;

@Service
@AllArgsConstructor
@KafkaListener(topics = ITEMS_FOR_SELL_TOPIC)
public class ItemsForSellTopicListener {

    private final SimpMessagingTemplate broker;

    @KafkaHandler
    void on(ItemsForSell evt) {

        broker.convertAndSend("/topic/events",
                evt,
                Map.of("event-type", evt.getClass().getSimpleName()));
    }
}
