package com.stepanov.messaging.listener;

import com.stepanov.kafka.events.topics.stock.ItemsForSellCmd;
import com.stepanov.service.StockService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.stepanov.kafka.topics.KafkaTopics.GATEWAY_COMMAND_STOCK_TOPIC;

@Service
@AllArgsConstructor
@Slf4j
@KafkaListener(topics = GATEWAY_COMMAND_STOCK_TOPIC)
public class GatewayCmdStockListener {

    private final StockService stockService;

    @KafkaHandler
    public void on(@NonNull ItemsForSellCmd evt) {
        stockService.itemsForSell();
    }

}
