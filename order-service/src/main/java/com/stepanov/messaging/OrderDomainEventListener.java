package com.stepanov.messaging;

import com.stepanov.kafka.events.OrderCancelled;
import com.stepanov.kafka.events.OrderPriceUpdate;
import com.stepanov.kafka.events.OrderReserved;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_UPDATED_TOPIC;

@Component
@RequiredArgsConstructor
public class OrderDomainEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /*
        Listens to changes in DomainEvents in OrderEntity and publish events to Kafka
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderPriceUpdate(OrderPriceUpdate evt) {
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderReserved(OrderReserved evt) {
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderCancelled(OrderCancelled evt) {
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, evt.orderId().toString(), evt);
    }
}
