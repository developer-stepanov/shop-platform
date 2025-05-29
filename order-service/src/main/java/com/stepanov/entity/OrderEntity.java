package com.stepanov.entity;

import com.stepanov.enums.OrderDetails;
import com.stepanov.enums.Currency;
import com.stepanov.enums.OrderStatus;
import com.stepanov.kafka.events.OrderCancelled;
import com.stepanov.kafka.events.OrderPriceUpdate;
import com.stepanov.kafka.events.OrderReserved;

import com.stepanov.kafka.events.PayUntil;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
//extends AbstractAggregateRoot to use @DomainEvents aggregator to send it to OrderDomainEventListener
public class OrderEntity extends AbstractAggregateRoot<OrderEntity> {

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "orderEntity",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItemEntity> items = new ArrayList<>();

    @PrePersist void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate void onUpdate() {
        updatedAt = Instant.now();
    }

    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrderEntity(this);
    }

    public void applyPrice(String sku, BigDecimal unitPrice) {
        Optional<OrderItemEntity> order = this.items.stream().filter(it -> sku.equals(it.getSku())).findFirst();
        order.ifPresent(o -> o.setUnitPrice(unitPrice));

        registerEvent(OrderPriceUpdate.builder()
                            .orderId(this.id)
                            .sku(sku)
                            .unitPrice(unitPrice)
                            .build());
    }

    public void updateOrderStatus(OrderStatus newStatus) {

        if (this.status != newStatus)
            this.status = newStatus;

        registerEvent(OrderReserved.builder()
                                .orderId(this.id)
                                .orderStatus(this.status)
                                .build());
    }

    public void applyPayUntil(Instant payUntil) {
        this.setPayUntil(payUntil);

        registerEvent(PayUntil.builder()
                            .orderId(this.id)
                            .payUntil(this.payUntil)
                            .build());
    }

    public void cancel(OrderDetails cancelReason) {
        if (this.status != OrderStatus.CANCELLED) {

            this.status = OrderStatus.CANCELLED;
            this.cancelReason = cancelReason;

            registerEvent(OrderCancelled.builder()
                                        .orderId(this.id)
                                        .orderStatus(this.status)
                                        .details(this.cancelReason)
                                        .build());
        }

        // throw exception order is already in status cancelled

    }

}
