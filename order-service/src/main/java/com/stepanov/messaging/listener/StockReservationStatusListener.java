package com.stepanov.messaging.listener;

import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import com.stepanov.kafka.events.topics.stock.OutOfStock;
import com.stepanov.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import static com.stepanov.kafka.topics.KafkaTopics.STOCK_RESERVATION_STATUS_TOPIC;

@Service
@AllArgsConstructor
@KafkaListener(topics = STOCK_RESERVATION_STATUS_TOPIC)
public class StockReservationStatusListener {

    private final OrderService orderService;

    @KafkaHandler
    public void on(@NonNull ConfirmationReservation evt, @NonNull @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        orderService.orderReserved(evt);
    }

    @KafkaHandler
    public void on(@NonNull OutOfStock evt, @NonNull @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        orderService.orderOutOfStock(evt);
    }
}
