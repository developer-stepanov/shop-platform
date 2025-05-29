package com.stepanov.service;

import com.stepanov.entity.OrderEntity;
import com.stepanov.enums.OrderDetails;
import com.stepanov.kafka.events.CreateOrder;
import com.stepanov.kafka.events.OrderPriceUpdate;
import com.stepanov.kafka.events.OrderReserved;
import com.stepanov.kafka.events.OutOfStock;
import com.stepanov.mapper.OrderMapper;
import com.stepanov.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {

    private static final long GAP_TO_PAY_MINUTES = 1;

    private final OrderRepository orderRepository;

    @Transactional
    public OrderEntity createOrder(CreateOrder evt) {
        return orderRepository.save(OrderMapper.toEntity(evt));
    }

    @Transactional
    public void updatePriceBySku(OrderPriceUpdate evt) {
        Optional<OrderEntity> order = orderRepository.findById(evt.orderId());

        order.ifPresent(it -> {
            it.applyPrice(evt.sku(), evt.unitPrice());
            //force to send to OrderDomainEventListener
            orderRepository.save(it);
        });
    }

    @Transactional
    public void orderReserved(OrderReserved evt) {
        Optional<OrderEntity> order = orderRepository.findById(evt.orderId());
        order.ifPresent(it -> {
            // fire OrderReserved
            it.updateOrderStatus(evt.orderStatus());
            //fire PayUntil
            it.applyPayUntil(resolvePayUntilTime());
            //force to send to OrderDomainEventListener
            orderRepository.save(it);
        });
    }

    @Transactional
    public void orderOutOfStock(OutOfStock evt) {
        Optional<OrderEntity> order = orderRepository.findById((evt.orderId()));
        order.ifPresent(it -> {
            it.cancel(OrderDetails.OUT_OF_STOCK);
            //force to send to OrderDomainEventListener
            orderRepository.save(it);
        });
    }

    private static Instant resolvePayUntilTime() {
        return Instant.now().plus(Duration.ofMinutes(GAP_TO_PAY_MINUTES));
    }
}
