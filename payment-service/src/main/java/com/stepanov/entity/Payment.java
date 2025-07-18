package com.stepanov.entity;

import com.stepanov.enums.Currency;
import com.stepanov.enums.PaymentMethod;
import com.stepanov.enums.PaymentStatus;
import com.stepanov.kafka.events.topics.payments.CheckoutPaymentLink;
import com.stepanov.kafka.events.topics.payments.PaymentSuccessful;
import com.stripe.model.checkout.Session;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends AbstractAggregateRoot<Payment> {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)   // Hibernate generates UUID-v7
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    @Column(name = "total_payment", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPayment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.STRIPE_CHECKOUT;

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    @Column(name = "stripe_payment_intent")
    private String stripePaymentIntent;

    @Column(name = "stripe_checkout_url")
    private String stripeCheckoutUrl;

    @Column(name = "fail_reason")
    private String failReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void updateWithCheckoutLink(Session s) {
        this.setStripeSessionId(s.getId());
        this.setStripeCheckoutUrl(s.getUrl());
        this.setPaymentStatus(PaymentStatus.LINK_SENT);

        registerEvent(CheckoutPaymentLink.builder()
                            .orderId(this.orderId)
                            .checkoutUrl(this.stripeCheckoutUrl)
                            .build());
    }

    public void markPaymentAsSucceeded(String stripePaymentIntentId) {
        this.setPaymentStatus(PaymentStatus.SUCCEEDED);
        this.setStripePaymentIntent(stripePaymentIntentId);

        registerEvent(PaymentSuccessful.builder()
                                        .orderId(this.orderId)
                                        .paymentStatus(this.paymentStatus)
                                        .build());
    }
}
