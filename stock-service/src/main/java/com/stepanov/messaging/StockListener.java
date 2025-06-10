package com.stepanov.messaging;

import com.stepanov.exceptions.OutOfStockException;
import com.stepanov.kafka.events.topics.orders.OrderForStock;
import com.stepanov.kafka.events.topics.orders.StockRelease;
import com.stepanov.kafka.events.topics.stock.ItemsForSellCmd;
import com.stepanov.service.StockService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.stepanov.kafka.topics.KafkaTopics.*;

@Component
@AllArgsConstructor
@Slf4j
public class StockListener {

    private final StockService stockService;

    @KafkaListener(topics = GATEWAY_COMMAND_FETCH_PRODUCTS_TOPIC)
    public void on(ItemsForSellCmd evt) {
        stockService.itemsForSell();
    }

    @KafkaListener(topics = ORDER_RESERVE_ORDER_TOPIC)
    public void on(OrderForStock evt) {
        try {
            stockService.reserveBy(evt);
        } catch(OutOfStockException e) {
            log.warn(e.getMessage());
        }
    }

    @KafkaListener(topics = ORDER_RELEASE_STOCK_TOPIC)
    public void on(StockRelease evt) {
       stockService.releaseStockBy(evt);
    }

}
