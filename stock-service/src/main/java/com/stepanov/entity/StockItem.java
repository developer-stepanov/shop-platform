package com.stepanov.entity;

import com.stepanov.enums.Currency;
import com.stepanov.kafka.events.topics.stock.StockItemUpdateQty;
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
@Table(name = "stocks")
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class StockItem extends AbstractAggregateRoot<StockItem>  {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(updatable = false, nullable = false, unique = true, length = 64)
    private String sku;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Currency currency;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQty;

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

    public void decreaseAvailableQty(int decreaseByQty) {
        this.availableQty = this.availableQty - decreaseByQty;

        registerEvent(StockItemUpdateQty.builder()
                                    .sku(this.sku)
                                    .qty(this.availableQty)
                                    .build());
    }

    public void increaseAvailableQty(int increaseByQty) {
        this.availableQty = this.availableQty + increaseByQty;

        registerEvent(StockItemUpdateQty.builder()
                                    .sku(this.sku)
                                    .qty(this.availableQty)
                                    .build());
    }

}
