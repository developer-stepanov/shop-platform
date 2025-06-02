package com.stepanov.scheduler;

import com.stepanov.kafka.events.OrderPaid;
import com.stepanov.kafka.events.PayUntil;
import com.stepanov.kafka.events.PaymentSuccessful;
import lombok.AllArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@AllArgsConstructor
public class SchedulerService {

    private final PaymentTimeoutScheduler paymentTimeoutScheduler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void startPaymentTimeoutScheduler(PayUntil evt) throws SchedulerException {
        paymentTimeoutScheduler.schedulePaymentTimeoutJob(evt.orderId(), evt.payUntil());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void stopPaymentTimeoutScheduler(OrderPaid evt) throws SchedulerException {
        paymentTimeoutScheduler.deleteJobBy(evt.orderId());
    }
}