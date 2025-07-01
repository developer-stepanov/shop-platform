package com.stepanov.messaging.listener;

import com.stepanov.kafka.events.topics.orders.OrderAccepted;
import com.stepanov.kafka.events.topics.orders.OrderCancelled;
import com.stepanov.kafka.events.topics.orders.OrderPaid;
import com.stepanov.kafka.events.topics.orders.OrderPaymentLinkUpdate;
import com.stepanov.kafka.events.topics.orders.OrderReserved;
import com.stepanov.kafka.events.topics.orders.OrderTableItem;
import com.stepanov.kafka.events.topics.orders.OrderTotalAmountUpdated;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_ORDER_SYNC_TOPIC;

/**
 * Listens to the order-sync topic and forwards every domain event to all
 * WebSocket clients that are subscribed to <code>/topic/events</code>.
 *
 * <p>The listener is annotated with {@link org.springframework.kafka.annotation.KafkaListener @KafkaListener},
 * while each overloaded {@code on(…)} method is an individual
 * {@link org.springframework.kafka.annotation.KafkaHandler @KafkaHandler}.  Spring Kafka dispatches the incoming
 * message to the handler whose parameter type matches the event payload.</p>
 *
 * <h2>Forwarding rules</h2>
 * <ul>
 *   <li><strong>Destination:</strong> {@code /topic/events}</li>
 *   <li><strong>Headers:</strong> Every outbound message carries a single
 *       header {@code event-type = <simple class name>} so that the
 *       front-end can switch on the event without inspecting the body.</li>
 * </ul>
 *
 * @author Maxim Stepanov
 */

@Service
@AllArgsConstructor
@KafkaListener(topics = ORDER_ORDER_SYNC_TOPIC)
public class OrderSyncListener {

    private final SimpMessagingTemplate broker;

    @KafkaHandler
    void on(@NonNull List<OrderTableItem> evt) {
        send(evt);
    }

    @KafkaHandler
    void on(@NonNull OrderAccepted evt, @NonNull @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        send(evt);
    }

    @KafkaHandler
    void on(@NonNull OrderTotalAmountUpdated evt, @NonNull @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        send(evt);
    }

    @KafkaHandler
    void on(@NonNull OrderReserved evt, @NonNull @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        send(evt);
    }

    @KafkaHandler
    void on(@NonNull OrderCancelled evt, @NonNull @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        send(evt);
    }

    @KafkaHandler
    void on(@NonNull OrderPaymentLinkUpdate evt, @NonNull @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        send(evt);
    }

    @KafkaHandler
    void on(@NonNull OrderPaid evt, @NonNull @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        send(evt);
    }

    private static Map<String, Object> mapHeaders(@NonNull String className) {
        return Map.of("event-type", className);
    }

    private void send(@NonNull Object payload) {
        final String destination = "/topic/events";
        broker.convertAndSend(destination, payload, mapHeaders(payload.getClass().getSimpleName()));
    }
}
