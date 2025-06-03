package com.stepanov.messaging;

import com.stepanov.kafka.events.*;

import com.stepanov.entity.StockItemEntity;
import com.stepanov.exceptions.OutOfStockException;
import com.stepanov.mapper.StockMapper;
import com.stepanov.repository.StockRepository;
import com.stepanov.service.StockService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.stepanov.kafka.topics.KafkaTopics.*;

@Component
@AllArgsConstructor
@Slf4j
public class StockEventsConsumer {

    private final StockEventsPublisher publisher;

    private final StockRepository repository;

    private final StockService stockService;

    @KafkaListener(topics = GIVE_ITEMS_FOR_SELL_TOPIC)
    public void onGiveItemsForSellEvent(GiveItemsForSell evt) {
        List<StockItemEntity> stockEntities = repository.findAll();
        ItemsForSell items = StockMapper.fromStockItems(stockEntities);

        publisher.publishStockItems(items);
    }

//    @KafkaListener(topics = ORDER_FOR_STOCK_TOPIC, groupId = "price-update")
//    @Transactional(readOnly = true)
//    public void onUpdatePriceEvent(OrderForStock evt) {
//        List<StockItemEntity> items = repository.findBySkuIn(evt.orderItems().stream()
//                                    .map(OrderItem::sku)
//                                    .toList());
//
//        List<OrderPriceUpdate.PriceBySku> priceBySkus = items.stream().map(it ->
//                                                                OrderPriceUpdate.PriceBySku.builder()
//                                                                .sku(it.getSku())
//                                                                .unitPrice(it.getUnitPrice())
//                                                                .build())
//                                                            .toList();
//
//        publisher.publishUpdatedPrice(OrderPriceUpdate.builder()
//                                        .orderId(evt.orderId())
//                                        .priceBySkus(priceBySkus)
//                                        .build());
//    }

    @KafkaListener(topics = ORDER_FOR_STOCK_TOPIC, groupId = "reserve-confirmation")
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
