package com.stepanov.messaging;

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_CREATED_TOPIC;
import static com.stepanov.kafka.topics.KafkaTopics.ORDER_PRICE_UPDATED_TOPIC;
import static com.stepanov.kafka.topics.KafkaTopics.ORDER_RESERVED_TOPIC;
import static com.stepanov.kafka.topics.KafkaTopics.OUT_OF_STOCK_TOPIC;

import com.stepanov.entity.OrderEntity;
import com.stepanov.enums.OrderDetails;
import com.stepanov.kafka.events.CreateOrder;
import com.stepanov.kafka.events.OrderAccepted;
import com.stepanov.kafka.events.OrderForStock;
import com.stepanov.kafka.events.OrderPriceUpdate;
import com.stepanov.kafka.events.OrderReserved;
import com.stepanov.kafka.events.OutOfStock;
import com.stepanov.mapper.OrderMapper;
import com.stepanov.repository.OrderRepository;

import lombok.AllArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

        orderEventsPublisher.publishOrderAccepted(orderAccepted);
        orderEventsPublisher.publishOrderForStock(orderForStock);
    }

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
