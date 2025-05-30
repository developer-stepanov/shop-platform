package com.stepanov.messaging.listener;

import com.stepanov.kafka.events.StockItemUpdateQty;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.stepanov.kafka.topics.KafkaTopics.STOCK_ITEM_UPDATE_TOPIC;

@Service
@AllArgsConstructor
@KafkaListener(topics = STOCK_ITEM_UPDATE_TOPIC)
public class StockItemUpdateTopicListener {

    private final SimpMessagingTemplate broker;

    @KafkaHandler
    void on(StockItemUpdateQty evt, @Header(KafkaHeaders.RECEIVED_KEY) String sku) {

        broker.convertAndSend("/topic/events",
                evt,
                Map.of("event-type", evt.getClass().getSimpleName()));
    }
}
