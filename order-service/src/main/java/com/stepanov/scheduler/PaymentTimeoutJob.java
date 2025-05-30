package com.stepanov.scheduler;

import com.stepanov.entity.OrderEntity;
import com.stepanov.enums.OrderDetails;
import com.stepanov.enums.OrderStatus;
import com.stepanov.exceptions.NotFoundEntityException;
import com.stepanov.kafka.events.OrderItem;
import com.stepanov.kafka.events.StockRelease;
import com.stepanov.messaging.OrderEventsPublisher;
import com.stepanov.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.stepanov.scheduler.PaymentTimeoutScheduler.ORDER_ID_JOB_KEY;

@Component
@AllArgsConstructor
public class PaymentTimeoutJob implements Job {

    private final OrderEventsPublisher orderEventsPublisher;

    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        UUID orderId = UUID.fromString(jobExecutionContext.getMergedJobDataMap().getString(ORDER_ID_JOB_KEY));

        OrderEntity orderEntity = orderRepository.findById(orderId)
                                                    .orElseThrow(() ->
                                                            throwNotFoundEntityException(
                                                                    OrderEntity.class.getSimpleName(),
                                                                    orderId.toString()
                                                            ));

        if (orderEntity.getStatus() != OrderStatus.RESERVED) {
            return;
        }

        List<OrderItem> orderItemList = orderEntity.getItems().stream()
                                                        .map(it ->
                                                                OrderItem.builder()
                                                                        .sku(it.getSku())
                                                                        .qty(it.getQty()).build()
                                                        )
                                                        .toList();

        orderEntity.cancel(OrderDetails.NOT_PAID);
        orderRepository.save(orderEntity);

        orderEventsPublisher.publishStockRelease(StockRelease.builder()
                                                            .orderId(orderId)
                                                            .orderItems(orderItemList)
                                                            .build());
    }

    private static NotFoundEntityException throwNotFoundEntityException(String className, String orderId) {
        return new NotFoundEntityException(String.format("Not found %s by orderId %s", className, orderId));
    }
}
