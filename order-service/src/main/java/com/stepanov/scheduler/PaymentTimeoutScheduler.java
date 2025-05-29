package com.stepanov.scheduler;

import lombok.AllArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentTimeoutScheduler {

    public static final String PAYMENT_TIMEOUT_KEY = "PAYMENT-TIMEOUT";

    public static final String ORDER_ID_JOB_KEY = "ORDER_ID";

    private final Scheduler scheduler;

    public void schedulePaymentTimeoutJob(UUID orderId, Instant payUntil) throws SchedulerException {
        JobKey jobKey = resolveJobKey(orderId);

        Trigger trigger = TriggerBuilder.newTrigger()
                                        .forJob(jobKey)
                                        .startAt(Date.from(payUntil))
                                        .build();

        JobDetail jobDetail = JobBuilder.newJob(PaymentTimeoutJob.class)
                .withIdentity(jobKey)
                .usingJobData(ORDER_ID_JOB_KEY, orderId.toString())
                .storeDurably()
                .build();

        scheduler.scheduleJob(jobDetail, trigger);

    }

    public void deleteJobBy(UUID orderId) throws SchedulerException {
        scheduler.deleteJob(resolveJobKey(orderId));
    }

    private static JobKey resolveJobKey(UUID orderId) {
        return JobKey.jobKey(String.format("%s_%s", PAYMENT_TIMEOUT_KEY, orderId));
    }

}
