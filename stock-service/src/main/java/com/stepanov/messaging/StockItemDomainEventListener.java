package com.stepanov.messaging;

import com.stepanov.kafka.events.topics.stock.StockItemUpdateQty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class StockItemDomainEventListener {

    private final StockEventsPublisher publisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishStockItemAvailableQtyChanged(StockItemUpdateQty evt) {
        publisher.publishStockItemAvailableQtyChanged(evt);
    }
}
