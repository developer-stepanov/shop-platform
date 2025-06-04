package com.stepanov.messaging;

import com.stepanov.kafka.events.topics.stock.StockItemUpdateQty;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.stepanov.kafka.topics.KafkaTopics.STOCK_ITEM_UPDATE_TOPIC;

@Component
@RequiredArgsConstructor
public class StockItemDomainEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishStockItemAvailableQtyChanged(StockItemUpdateQty evt) {
        kafkaTemplate.send(STOCK_ITEM_UPDATE_TOPIC, evt.sku(), evt);
    }
}
