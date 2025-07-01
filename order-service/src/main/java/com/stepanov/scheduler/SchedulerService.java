package com.stepanov.scheduler;

import com.stepanov.kafka.events.topics.orders.OrderPaid;
import com.stepanov.kafka.events.topics.orders.PayUntil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@AllArgsConstructor
public class SchedulerService {

    private final PaymentTimeoutScheduler paymentTimeoutScheduler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void startPaymentTimeoutScheduler(@NonNull PayUntil evt) throws SchedulerException {
        paymentTimeoutScheduler.schedulePaymentTimeoutJob(evt.orderId(), evt.payUntil());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void stopPaymentTimeoutScheduler(@NonNull OrderPaid evt) throws SchedulerException {
        paymentTimeoutScheduler.deleteJobBy(evt.orderId());
    }
}