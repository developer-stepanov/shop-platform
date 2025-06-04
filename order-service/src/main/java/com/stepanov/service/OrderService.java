package com.stepanov.service;

import com.stepanov.entity.OrderEntity;
import com.stepanov.enums.OrderDetails;
import com.stepanov.kafka.events.*;
import com.stepanov.mapper.OrderMapper;
import com.stepanov.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.apache.catalina.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {

    private static final long GAP_TO_PAY_MINUTES = 5;

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderTableItem> fetchOrderItems() {
        return OrderMapper.fromEntity(orderRepository.findAll());
    }

    @Transactional
    public OrderEntity createOrder(CreateOrder evt) {
        return orderRepository.save(OrderMapper.toEntity(evt));
    }

    @Transactional
    public void updatePriceBySku(OrderPriceUpdate evt) {
        Optional<OrderEntity> order = orderRepository.findById(evt.orderId());

        order.ifPresent(it -> {
            it.applyUnitPriceAndTotalAmount(evt.priceBySkus());
            //force to send to OrderDomainEventListener
            orderRepository.save(it);
        });
    }

    @Transactional
    public void orderReserved(ConfirmationReservation evt) {
        Optional<OrderEntity> order = orderRepository.findById(evt.orderId());
        order.ifPresent(it -> {
            // fire OrderReserved
            it.applyReservedStatus(evt);
            //fire PayUntil
            it.applyPayUntilTime(resolvePayUntilTime());
            //force to send to OrderDomainEventListener
            orderRepository.save(it);
        });
    }

    @Transactional
    public void orderOutOfStock(OutOfStock evt) {
        Optional<OrderEntity> order = orderRepository.findById((evt.orderId()));
        order.ifPresent(it -> {
            it.applyCanceledStatus(OrderDetails.OUT_OF_STOCK);
            //force to send to OrderDomainEventListener
            orderRepository.save(it);
        });
    }

    @Transactional
    public void paymentLink(PaymentLink evt) {
        Optional<OrderEntity> order = orderRepository.findById(evt.orderId());

        order.ifPresent(it -> {
            it.applyPaymentLink(evt.checkoutUrl());
            orderRepository.save(it);
        });
    }

    @Transactional
    public void orderPaymentSucceeded(PaymentSuccessful evt) {
        Optional<OrderEntity> order = orderRepository.findById(evt.orderId());

        order.ifPresent(it -> {
            it.applyPaidStatus();
            orderRepository.save(it);
        });
    }

    private static Instant resolvePayUntilTime() {
        return Instant.now().plus(Duration.ofMinutes(GAP_TO_PAY_MINUTES));
    }
}
