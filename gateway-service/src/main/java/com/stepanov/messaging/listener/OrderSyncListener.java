package com.stepanov.messaging.listener;

import com.stepanov.kafka.events.topics.orders.*;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_ORDER_SYNC_TOPIC;

@Service
@AllArgsConstructor
@KafkaListener(topics = ORDER_ORDER_SYNC_TOPIC)
public class OrderSyncListener {

    private final SimpMessagingTemplate broker;

    @KafkaHandler
    void on(List<OrderTableItem> evt) {
        send(evt);
    }

    @KafkaHandler
    void on(OrderAccepted evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        send(evt);
    }

    @KafkaHandler
    void on(OrderTotalAmountUpdated evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        send(evt);
    }

    @KafkaHandler
    void on(OrderReserved evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        send(evt);
    }

    @KafkaHandler
    void on(OrderCancelled evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        send(evt);
    }

    @KafkaHandler
    void on(OrderPaymentLinkUpdate evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        send(evt);
    }

    @KafkaHandler
    void on(OrderPaid evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
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
