package com.stepanov.scheduler;

import com.stepanov.enums.OrderDetails;
import com.stepanov.enums.OrderStatus;
import com.stepanov.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.stepanov.scheduler.PaymentTimeoutScheduler.ORDER_ID_JOB_KEY;

@Component
@AllArgsConstructor
public class PaymentTimeoutJob implements Job {

    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        UUID orderId = UUID.fromString(jobExecutionContext.getMergedJobDataMap().getString(ORDER_ID_JOB_KEY));

        orderRepository.findById(orderId).ifPresent(o -> {
            if (o.getStatus() == OrderStatus.RESERVED) {
                o.cancel(OrderDetails.NOT_PAID);
                orderRepository.save(o);
            }
        });
    }
}
