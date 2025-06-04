package com.stepanov.messaging;

import com.stepanov.kafka.events.*;

import com.stepanov.exceptions.OutOfStockException;
import com.stepanov.service.StockService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.stepanov.kafka.topics.KafkaTopics.*;

@Component
@AllArgsConstructor
@Slf4j
public class StockEventsConsumer {

    private final StockService stockService;

    @KafkaListener(topics = GIVE_ITEMS_FOR_SELL_TOPIC)
    public void onGiveItemsForSellEvent(ItemsForSellCmd evt) {
        stockService.itemsForSell();
    }

    @KafkaListener(topics = ORDER_FOR_STOCK_TOPIC)
    public void onReserveItemsEvent(OrderForStock evt) {
        try {
            stockService.reserveBy(evt);
        } catch(OutOfStockException e) {
            log.warn(e.getMessage());
        }
    }

    @KafkaListener(topics = STOCK_RELEASE_TOPIC)
    public void onStockReleaseEvent(StockRelease evt) {
       stockService.releaseStockBy(evt);
    }

}
