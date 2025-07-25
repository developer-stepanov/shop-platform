package com.stepanov.scheduler;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
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

    public void schedulePaymentTimeoutJob(@NonNull UUID orderId, @NonNull Instant payUntil) throws SchedulerException {
        final JobKey jobKey = resolveJobKey(orderId);

        final Trigger trigger = TriggerBuilder.newTrigger()
                                        .forJob(jobKey)
                                        .startAt(Date.from(payUntil))
                                        .build();

        final JobDetail jobDetail = JobBuilder.newJob(PaymentTimeoutJob.class)
                .withIdentity(jobKey)
                .usingJobData(ORDER_ID_JOB_KEY, orderId.toString())
                .storeDurably()
                .build();

        scheduler.scheduleJob(jobDetail, trigger);

    }

    public void deleteJobBy(@NonNull UUID orderId) throws SchedulerException {
        scheduler.deleteJob(resolveJobKey(orderId));
    }

    private static JobKey resolveJobKey(@NonNull UUID orderId) {
        return JobKey.jobKey(String.format("%s_%s", PAYMENT_TIMEOUT_KEY, orderId));
    }

}
