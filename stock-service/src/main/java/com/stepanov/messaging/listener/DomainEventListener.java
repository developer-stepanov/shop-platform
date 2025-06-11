package com.stepanov.messaging.listener;

import com.stepanov.kafka.events.topics.stock.StockItemUpdateQty;
import com.stepanov.messaging.publisher.StockPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DomainEventListener {

    private final StockPublisher publisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(StockItemUpdateQty evt) {
        publisher.publish(evt);
    }
}
