package com.stepanov.messaging.publisher;

import com.stepanov.kafka.events.topics.orders.OrderAccepted;
import com.stepanov.kafka.events.topics.orders.OrderCancelled;
import com.stepanov.kafka.events.topics.orders.OrderPaid;
import com.stepanov.kafka.events.topics.orders.OrderPaymentLinkUpdate;
import com.stepanov.kafka.events.topics.orders.OrderReserved;
import com.stepanov.kafka.events.topics.orders.OrderTableItem;
import com.stepanov.kafka.events.topics.orders.OrderTotalAmountUpdated;
import com.stepanov.kafka.events.topics.orders.ReserveStock;
import com.stepanov.kafka.events.topics.orders.StockRelease;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_COMMAND_STOCK_TOPIC;
import static com.stepanov.kafka.topics.KafkaTopics.ORDER_ORDER_SYNC_TOPIC;
import static com.stepanov.kafka.topics.KafkaTopics.ORDER_PREPARE_PAYMENT_TOPIC;

@Service
@AllArgsConstructor
public class OrderPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(@NonNull ReserveStock evt) {
        kafkaTemplate.send(ORDER_COMMAND_STOCK_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(@NonNull StockRelease evt) {
        kafkaTemplate.send(ORDER_COMMAND_STOCK_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(@NonNull ConfirmationReservation evt) {
        kafkaTemplate.send(ORDER_PREPARE_PAYMENT_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(@NonNull List<OrderTableItem> evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt);
    }

    public void publish(@NonNull OrderAccepted evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(@NonNull OrderTotalAmountUpdated evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(@NonNull OrderReserved evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(@NonNull OrderCancelled evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(@NonNull OrderPaymentLinkUpdate evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt.orderId().toString(), evt);
    }

    public void publish(@NonNull OrderPaid evt) {
        kafkaTemplate.send(ORDER_ORDER_SYNC_TOPIC, evt.orderId().toString(), evt);
    }

}
