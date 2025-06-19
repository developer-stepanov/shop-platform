package com.stepanov.entity;

import com.stepanov.enums.Currency;
import com.stepanov.enums.OrderDetails;
import com.stepanov.enums.OrderStatus;
import com.stepanov.kafka.events.topics.orders.OrderCancelled;
import com.stepanov.kafka.events.topics.orders.OrderPaid;
import com.stepanov.kafka.events.topics.orders.OrderPaymentLinkUpdate;
import com.stepanov.kafka.events.topics.orders.OrderTotalAmountUpdated;
import com.stepanov.kafka.events.topics.orders.PayUntil;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import com.stepanov.kafka.events.topics.stock.PaymentDetails;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
//extends AbstractAggregateRoot to use @DomainEvents aggregator to send it to OrderDomainEventListener
    public class Order extends AbstractAggregateRoot<Order> {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)   // Hibernate generates UUID-v7
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private OrderStatus status;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Currency currency;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "pay_until")
    private Instant payUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_reason")
    private OrderDetails cancelReason;

    @Column(name = "payment_link")
    private String paymentLink;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "orderEntity",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate void onUpdate() {
        updatedAt = Instant.now();
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrderEntity(this);
    }

    public void applyReservedStatus(ConfirmationReservation evt) {

        if (evt.orderStatus() == OrderStatus.RESERVED) {
            this.status = evt.orderStatus();

            this.totalAmount = evt.paymentDetails().totalPayment();
            this.currency = evt.paymentDetails().currency();

            this.items.forEach(it -> {
                final String sku = it.getSku();
                final BigDecimal unitPrice = evt.unitPrices().stream()
                            .filter(p -> sku.equals(p.sku()))
                            .map(ConfirmationReservation.UnitPrice::unitPrice)
                            .findFirst().orElseThrow();

                it.setUnitPrice(unitPrice);
            });


            registerEvent(ConfirmationReservation.builder()
                                        .orderId(this.id)
                                        .orderStatus(this.status)
                                        .paymentDetails(PaymentDetails.builder()
                                                .totalPayment(this.totalAmount)
                                                .currency(this.currency)
                                                .build())
                                        .build());

            registerEvent(OrderTotalAmountUpdated.builder()
                                        .orderId(this.id)
                                        .totalAmount(this.totalAmount.longValue())
                                        .build());
        }
    }

    public void applyPayUntilTime(Instant payUntil) {
        this.setPayUntil(payUntil);

        registerEvent(PayUntil.builder()
                            .orderId(this.id)
                            .payUntil(this.payUntil)
                            .build());
    }

    public void applyCanceledStatus(OrderDetails cancelReason) {
        if (this.status != OrderStatus.CANCELLED) {

            this.status = OrderStatus.CANCELLED;
            this.cancelReason = cancelReason;

            registerEvent(OrderCancelled.builder()
                                        .orderId(this.id)
                                        .orderStatus(this.status)
                                        .details(this.cancelReason)
                                        .build());
        }
    }

    public void applyPaymentLink(String paymentLink) {
        if (this.status != OrderStatus.CANCELLED) {
            this.paymentLink = paymentLink;

            registerEvent(OrderPaymentLinkUpdate.builder()
                                .orderId(this.id)
                                .paymentLink(this.paymentLink)
                                .build());
        }
    }

    public void applyPaidStatus() {
        if (this.status != OrderStatus.CANCELLED) {
            this.status = OrderStatus.PAID;

            registerEvent(OrderPaid.builder()
                    .orderId(this.id)
                    .orderStatus(this.status)
                    .build());
        }
    }
}
