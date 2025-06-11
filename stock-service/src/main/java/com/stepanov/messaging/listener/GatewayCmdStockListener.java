package com.stepanov.messaging.listener;

import com.stepanov.exceptions.OutOfStockException;
import com.stepanov.kafka.events.topics.orders.OrderForStock;
import com.stepanov.kafka.events.topics.orders.StockRelease;
import com.stepanov.kafka.events.topics.stock.ItemsForSellCmd;
import com.stepanov.service.StockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static com.stepanov.kafka.topics.KafkaTopics.*;

@Service
@AllArgsConstructor
@Slf4j
@KafkaListener(topics = GATEWAY_COMMAND_STOCK_TOPIC)
public class GatewayCmdStockListener {

    private final StockService stockService;

    @KafkaHandler
    public void on(ItemsForSellCmd evt) {
        stockService.itemsForSell();
    }

}
