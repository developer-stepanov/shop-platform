package com.stepanov.service;

import com.stepanov.entity.Order;
import com.stepanov.enums.OrderDetails;
import com.stepanov.kafka.events.topics.orders.CreateOrder;
import com.stepanov.kafka.events.topics.orders.OrderTableItem;
import com.stepanov.kafka.events.topics.payments.CheckoutPaymentLink;
import com.stepanov.kafka.events.topics.payments.PaymentSuccessful;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import com.stepanov.kafka.events.topics.stock.OutOfStock;
import com.stepanov.mapper.OrderMapper;
import com.stepanov.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {

    private static final long GAP_TO_PAY_MINUTES = 5;

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderTableItem> fetchOrderItems() {
        return OrderMapper.fromEntity(orderRepository.findAll());
    }

    @Transactional
    public Order createOrder(@NonNull CreateOrder evt) {
        final Order order = orderRepository.save(OrderMapper.toEntity(evt));
        log.info("Order, id: [{}] created for customer, id: [{}].", order.getId(),
                                                          order.getCustomerId());
        return order;
    }

    @Transactional
    public void orderReserved(@NonNull ConfirmationReservation evt) {
        final Optional<Order> order = orderRepository.findById(evt.orderId());
        order.ifPresentOrElse(it -> {
            it.applyReservedStatusAndTotalAmount(evt);
            it.applyPayUntilTime(resolvePayUntilTime());
            orderRepository.save(it);

            log.info("Order [{}] reserved with status [{}]. Pay until: [{}]", it.getId(),
                                                                              it.getStatus(),
                                                                              it.getPayUntil());
        }, () -> log.warn("Received reservation event for missing order, id: [{}]. Event: {}", evt.orderId(),
                                                                                          evt));
    }

    @Transactional
    public void orderOutOfStock(@NonNull OutOfStock evt) {
        final Optional<Order> order = orderRepository.findById((evt.orderId()));
        order.ifPresentOrElse(it -> {
            it.applyCanceledStatus(OrderDetails.OUT_OF_STOCK);
            //force to send to OrderDomainEventListener
            orderRepository.save(it);

            log.info("Order [{}] canceled with reason [{}].", it.getId(),
                                                              OrderDetails.OUT_OF_STOCK);
        }, () -> log.warn("Received out-of-stock event for missing order, id: [{}]. Event: {}", evt.orderId(),
                                                                                           evt));
    }

    @Transactional
    public void paymentLink(@NonNull CheckoutPaymentLink evt) {
        final Optional<Order> order = orderRepository.findById(evt.orderId());
        order.ifPresentOrElse(it -> {
            it.applyPaymentLink(evt.checkoutUrl());
            orderRepository.save(it);

            log.info("Added payment link for order [{}]: {}", it.getId(),
                                                              evt.checkoutUrl());
        }, () -> log.warn("Payment link event for non-existent order, id: [{}]. Event: {}", evt.orderId(),
                                                                                            evt));
    }

    @Transactional
    public void orderPaymentSucceeded(@NonNull PaymentSuccessful evt) {
        final Optional<Order> order = orderRepository.findById(evt.orderId());
        order.ifPresentOrElse(it -> {
            it.applyPaidStatus();
            orderRepository.save(it);

            log.info("Marked order [{}] as paid.", it.getId());
        }, () -> log.warn("Payment success event for non-existent order, id: [{}]. Event: {}", evt.orderId(),
                                                                                               evt));
    }

    private static Instant resolvePayUntilTime() {
        return Instant.now().plus(Duration.ofMinutes(GAP_TO_PAY_MINUTES));
    }
}
