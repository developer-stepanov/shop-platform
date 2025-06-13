package com.stepanov.messaging.listener;

import com.stepanov.kafka.events.topics.stock.ItemsForSell;
import com.stepanov.kafka.events.topics.stock.StockItemUpdateQty;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.stepanov.kafka.topics.KafkaTopics.STOCK_PRODUCT_SYNC_TOPIC;

@Service
@AllArgsConstructor
@KafkaListener(topics = STOCK_PRODUCT_SYNC_TOPIC)
public class ProductSyncListener {

    private final SimpMessagingTemplate broker;

    @KafkaHandler
    void on(ItemsForSell evt) {
        send(evt);
    }

    @KafkaHandler
    void on(StockItemUpdateQty evt, @Header(KafkaHeaders.RECEIVED_KEY) String sku) {
        send(evt);
    }

    private static Map<String, Object> mapHeaders(String className) {
        return Map.of("event-type", className);
    }

    private void send(Object payload) {
        final String destination = "/topic/events";
        broker.convertAndSend(destination, payload, mapHeaders(payload.getClass().getSimpleName()));
    }
}
