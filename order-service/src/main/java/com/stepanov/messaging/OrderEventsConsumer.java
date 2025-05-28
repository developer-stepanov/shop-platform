package com.stepanov.messaging;

import com.stepanov.entity.OrderEntity;
import com.stepanov.enums.OrderDetails;
import com.stepanov.kafka.events.*;
import com.stepanov.mapper.OrderMapper;
import com.stepanov.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.stepanov.kafka.topics.KafkaTopics.*;

@Component
@AllArgsConstructor
public class OrderEventsConsumer {

    private final OrderEventsPublisher orderEventsPublisher;

    private final OrderRepository orderRepository;

    @KafkaListener(topics = ORDER_CREATED_TOPIC)
    @Transactional // make atomic with Kafka changes
    public void onCreateOrder(CreateOrder evt, @Header(KafkaHeaders.RECEIVED_KEY) String clientRequestId) {

        OrderEntity orderEntity = OrderMapper.toEntity(evt);
        orderEntity = orderRepository.save(orderEntity);

        OrderAccepted orderAccepted = OrderMapper.toOrderAccepted(evt.clientRequestId(), orderEntity);
        OrderForStock orderForStock = OrderMapper.toOrderForStock(orderEntity);

        /*
            Emit event to Gateway service
         */
        orderEventsPublisher.publishOrderAccepted(orderAccepted);

        /*
            Emit event to Stock service
         */
        orderEventsPublisher.publishOrderForStock(orderForStock);

        System.out.println("EVENT HAS BEEN RECEIVED: " + evt + ", clientRequestId: " + clientRequestId);
    }

//    @KafkaListener(topics = ORDER_FOR_STOCK_TOPIC)
//    public void onOrderForStockEvent(OrderForStock event, @Header(KafkaHeaders.RECEIVED_KEY) String sessionId) {
//        System.out.println("EVENT HAS BEEN RECEIVED: " + event + ", sessionId " + sessionId);
//    }

    @KafkaListener(topics = ORDER_PRICE_UPDATED_TOPIC)
    @Transactional
    public void onOrderPriceUpdated(OrderPriceUpdate evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        Optional<OrderEntity> order = orderRepository.findById(evt.orderId());

        order.ifPresent(it -> {
            it.applyPrice(evt.sku(), evt.unitPrice());
            //force to send to OrderDomainEventListener
            orderRepository.save(it);
        });

        // add log that order can not be found
    }

    @KafkaListener(topics = ORDER_RESERVED_TOPIC)
    @Transactional
    public void onOrderReserved(OrderReserved evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        Optional<OrderEntity> order = orderRepository.findById(evt.orderId());
        order.ifPresent(it -> {
            it.updateOrderStatus(evt.orderStatus());
            //force to send to OrderDomainEventListener
            orderRepository.save(it);
        });

        // add log order can not be found
    }

    @KafkaListener(topics = OUT_OF_STOCK_TOPIC)
    @Transactional
    public void onOrderOutOfStock(OutOfStock evt, @Header(KafkaHeaders.RECEIVED_KEY) String orderId) {
        Optional<OrderEntity> order = orderRepository.findById((evt.orderId()));
        order.ifPresent(it -> {
            it.cancel(OrderDetails.OUT_OF_STOCK);
            //force to send to OrderDomainEventListener
            orderRepository.save(it);
        });

        // add log order can not be found
    }
}
