package com.stepanov.scheduler;

import com.stepanov.kafka.events.PayUntilEvent;
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
    public void startPaymentTimeoutScheduler(PayUntilEvent payUntil) throws SchedulerException {
        paymentTimeoutScheduler.schedulePaymentTimeoutJob(payUntil.orderId(), payUntil.payUntil());
    }
}