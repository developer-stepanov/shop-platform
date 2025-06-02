package com.stepanov.entity;

import com.stepanov.enums.Currency;
import com.stepanov.enums.PaymentMethod;
import com.stepanov.enums.PaymentStatus;
import com.stepanov.kafka.events.PaymentLink;
import com.stepanov.kafka.events.PaymentSuccessful;
import com.stripe.model.checkout.Session;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class PaymentEntity extends AbstractAggregateRoot<PaymentEntity> {

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

        registerEvent(PaymentLink.builder()
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
