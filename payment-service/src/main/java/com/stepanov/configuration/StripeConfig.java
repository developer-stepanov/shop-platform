package com.stepanov.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
@Getter @Setter
public class StripeConfig {
    @Value("${stripe.api-key}")
    private String apiKey;
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostConstruct
    void init() { com.stripe.Stripe.apiKey = apiKey; }
}