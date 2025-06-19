package com.stepanov.messaging.listener;

import com.stepanov.exceptions.OutOfStockException;
import com.stepanov.kafka.events.topics.orders.OrderForStock;
import com.stepanov.kafka.events.topics.orders.StockRelease;
import com.stepanov.service.StockService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_COMMAND_STOCK_TOPIC;

@Component
@AllArgsConstructor
@Slf4j
@KafkaListener(topics = ORDER_COMMAND_STOCK_TOPIC)
public class OrderCmdStockListener {

    private final StockService stockService;

    @KafkaHandler
    public void on(OrderForStock evt) {
        try {
            stockService.reserveBy(evt);
        } catch(OutOfStockException e) {
            log.warn(e.getMessage());
        }
    }

    @KafkaHandler
    public void on(StockRelease evt) {
       stockService.releaseStockBy(evt);
    }

}
