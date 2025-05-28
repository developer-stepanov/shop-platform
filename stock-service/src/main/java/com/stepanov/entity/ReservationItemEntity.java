package com.stepanov.entity;

import com.stepanov.enums.ReservationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reservation_items",
        uniqueConstraints = @UniqueConstraint(name="uk_order_sku",
                                                columnNames = {"order_id","sku"}))
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class ReservationItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, name = "order_id", nullable = false)
    private UUID orderId;

    @Column(updatable = false, nullable = false, length = 64)
    private String sku;

    @Column(nullable = false)
    private BigDecimal qty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus reservationStatus;

    @Column(name = "reserved_at", nullable = false, updatable = false)
    private Instant reservedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        reservedAt = updatedAt = Instant.now();
    }

    @PreUpdate void onUpdate() {
        updatedAt = Instant.now();
    }

}
