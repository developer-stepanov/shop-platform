package com.stepanov.messaging.error_handler;

import com.stepanov.exceptions.OutOfStockException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler errorHandler() {
        // Retry other exceptions up to 3 times, 1 second delay
        final FixedBackOff backOff = new FixedBackOff(1000L, 3);

        final DefaultErrorHandler handler = new DefaultErrorHandler(backOff);

        // These exceptions will not be retried, and will be SKIPPED after logging
        handler.addNotRetryableExceptions(
                OutOfStockException.class,
                EntityNotFoundException.class,
                NullPointerException.class
        );

        handler.setRetryListeners((record, ex, attempt) -> {
            Throwable realExep = ex;

            if (ex.getCause() != null) {
                realExep = ex.getCause();
            }

            if (realExep instanceof OutOfStockException) {
                log.error("OutOfStockException: {}", realExep.getMessage());
            } else if (realExep instanceof EntityNotFoundException) {
                log.error("EntityNotFoundException: {}", realExep.getMessage());
            } else if (realExep instanceof NullPointerException) {
                log.error("NullPointerException: {}", realExep.getMessage());
            } else {
                log.error("Other Exception (attempt {}): {}", attempt, realExep.getMessage());
            }
        });

        return handler;
    }

}

