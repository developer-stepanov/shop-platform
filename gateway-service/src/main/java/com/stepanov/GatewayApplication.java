package com.stepanov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Bootstrap class for the Gateway service.
 *
 * <p>This is the entry‐point of the Spring Boot application that fronts the
 * entire shop platform. Aside from loading the standard Spring context,
 * the {@link EnableKafka @EnableKafka} annotation activates Spring for Apache Kafka
 * so that components can consume and publish domain events without any extra
 * configuration.</p>
 *
 * @author  Maxim Stepanov
 */

@SpringBootApplication
@EnableKafka
public class GatewayApplication {
    public static void main(String[] args) {
	    SpringApplication.run(GatewayApplication.class, args);
    }
}
