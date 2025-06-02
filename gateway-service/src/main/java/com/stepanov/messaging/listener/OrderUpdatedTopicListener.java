package com.stepanov.messaging.listener;

import com.stepanov.kafka.events.*;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_UPDATED_TOPIC;

@Service
@AllArgsConstructor
@KafkaListener(topics = ORDER_UPDATED_TOPIC)
public class OrderUpdatedTopicListener {

    private final SimpMessagingTemplate broker;

    @KafkaHandler
    void on(OrderPriceUpdate evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {

        broker.convertAndSend("/topic/events",
                evt,
                Map.of("event-type", evt.getClass().getSimpleName()));
    }

    @KafkaHandler
    void on(OrderReserved evt,
            @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {

        broker.convertAndSend("/topic/events",
                evt,
                Map.of("event-type", evt.getClass().getSimpleName()));
    }

    @KafkaHandler
    void on(OrderCancelled evt,
            @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {

        broker.convertAndSend("/topic/events",
                evt,
                Map.of("event-type", evt.getClass().getSimpleName()));
    }

    @KafkaHandler
    void on(OrderPaymentLinkUpdate evt,
            @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {

        broker.convertAndSend("/topic/events",
                evt,
                Map.of("event-type", evt.getClass().getSimpleName()));
    }

    @KafkaHandler
    void on(OrderPaid evt,
            @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {

        broker.convertAndSend("/topic/events",
                evt,
                Map.of("event-type", evt.getClass().getSimpleName()));
    }
}
