package com.stepanov.scheduler;

import com.stepanov.entity.Order;
import com.stepanov.enums.OrderDetails;
import com.stepanov.enums.OrderStatus;
import com.stepanov.exceptions.NotFoundEntityException;
import com.stepanov.kafka.events.topics.orders.OrderItem;
import com.stepanov.kafka.events.topics.orders.StockRelease;
import com.stepanov.messaging.publisher.OrderPublisher;
import com.stepanov.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.stepanov.scheduler.PaymentTimeoutScheduler.ORDER_ID_JOB_KEY;

@Component
@AllArgsConstructor
@Slf4j
public class PaymentTimeoutJob implements Job {

    private final OrderPublisher orderEventsPublisher;

    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public void execute(@NonNull JobExecutionContext jobExecutionContext) {
        final UUID orderId = UUID.fromString(jobExecutionContext.getMergedJobDataMap().getString(ORDER_ID_JOB_KEY));
        log.info("Starting payment expiration job for order, id: [{}].", orderId);

        final Order orderEntity = orderRepository.findById(orderId)
                                                    .orElseThrow(() ->
                                                            throwNotFoundEntityException(
                                                                    Order.class.getSimpleName(),
                                                                    orderId.toString()
                                                            ));

        if (orderEntity.getStatus() != OrderStatus.RESERVED) {
            log.info("Order, id: [{}] status is [{}]; no action taken (only RESERVED orders are expired).", orderId,
                                                                                            orderEntity.getStatus());
            return;
        }

        final List<OrderItem> orderItemList = orderEntity.getItems().stream()
                                                        .map(it ->
                                                                OrderItem.builder()
                                                                        .sku(it.getSku())
                                                                        .qty(it.getQty()).build()
                                                        )
                                                        .toList();

        orderEntity.applyCanceledStatus(OrderDetails.NOT_PAID);
        orderRepository.save(orderEntity);

        log.info("Order, id: [{}] canceled due to non-payment. Status set to [{}].", orderId,
                                                                                     OrderDetails.NOT_PAID);

        orderEventsPublisher.publish(StockRelease.builder()
                                                .orderId(orderId)
                                                .orderItems(orderItemList)
                                                .build());

        log.info("Published stock release event for order, id: [{}] with [{}] items.", orderId, orderItemList.size());
    }

    private static NotFoundEntityException throwNotFoundEntityException(@NonNull String className,
                                                                        @NonNull String orderId) {
        return new NotFoundEntityException(String.format("Not found %s by orderId %s", className, orderId));
    }
}
